package vegabobo.dsusideloader.preparation

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import android.util.Log
import java.io.Closeable
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import net.jpountz.lz4.LZ4FrameInputStream
import org.tukaani.xz.XZInputStream
import vegabobo.dsusideloader.core.StorageManager

class FileUnPacker(
    private val storageManager: StorageManager,
    private val inputFile: Uri,
    outputFile: String,
    private val installationJob: Job,
    private val onProgressChange: (Float) -> Unit,
) {

    private val finalFile: DocumentFile = storageManager.createDocumentFile(outputFile)

    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private val inputFileSize: Long = storageManager.getFilesizeFromUri(inputFile)

    init {
        outputStream = storageManager.openOutputStream(finalFile.uri)
        inputStream = storageManager.openInputStream(inputFile)
    }

    private class CountingInputStream(stream: InputStream) : FilterInputStream(stream) {
        var count: Long = 0
            private set

        override fun read(): Int {
            val result = super.read()
            if (result >= 0) count++
            return result
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            val result = super.read(b, off, len)
            if (result > 0) count += result
            return result
        }

        override fun skip(n: Long): Long {
            val result = super.skip(n)
            if (result > 0) count += result
            return result
        }
    }

    private fun copy(
        inputStr: InputStream,
        outputStr: OutputStream,
        onReadedBuffer: (Long) -> Unit,
    ) {
        val buffer = ByteArray(8 * 1024)
        var n: Int
        var readed: Long = 0
        while (inputStr.read(buffer).also { n = it } != -1 && !installationJob.isCancelled) {
            readed += n
            onReadedBuffer(readed)
            outputStr.write(buffer, 0, n)
        }
        try {
            inputStr.close()
        } catch (e: IOException) {
            Log.w("FileUnPacker", "Failed to close input stream", e)
        }
        try {
            outputStr.flush()
            outputStr.close()
        } catch (e: IOException) {
            Log.w("FileUnPacker", "Failed to flush/close output stream", e)
        }
    }

    fun pack(): Pair<Uri, Long> {
        val outStream = outputStream
        val inStream = inputStream
        if (outStream == null || inStream == null) {
            throw IllegalStateException("Streams not initialized")
        }
        try {
            copy(inStream, GZIPOutputStream(outStream)) {
                updateProgress(inputFileSize, it)
            }
        } finally {
            closeQuietly(inStream)
            closeQuietly(outStream)
        }
        val fileLength = storageManager.getFilesizeFromUri(finalFile.uri)
        return Pair(finalFile.uri, fileLength)
    }

    fun unpack(): Pair<Uri, Long> {
        val inStream = inputStream
            ?: throw IllegalStateException("Input stream not initialized")
        val outStream = outputStream
            ?: throw IllegalStateException("Output stream not initialized")

        val countingInputStream = CountingInputStream(inStream)
        val progressJob = CoroutineScope(installationJob + Dispatchers.IO).launch {
            while (!installationJob.isCancelled) {
                delay(200)
                if (!installationJob.isCancelled) {
                    updateProgress(inputFileSize, countingInputStream.count)
                }
            }
        }
        try {
            val archiveInputStream = with(storageManager.getFilenameFromUri(inputFile).lowercase()) {
                when {
                    endsWith("xz") -> XZInputStream(countingInputStream)
                    endsWith("gz") -> GZIPInputStream(countingInputStream)
                    endsWith("gzip") -> GZIPInputStream(countingInputStream)
                    endsWith("bz2") -> BZip2CompressorInputStream(countingInputStream)
                    endsWith("bzip2") -> BZip2CompressorInputStream(countingInputStream)
                    endsWith("lz4") -> {
                        try {
                            net.jpountz.lz4.LZ4FrameInputStream(countingInputStream)
                        } catch (e: Exception) {
                            Log.e("FileUnPacker", "LZ4 decompression failed: ${e.message}", e)
                            throw Exception("LZ4 decompression failed. The file may be corrupted or use an unsupported LZ4 format (legacy/block format). Only standard LZ4 framed format is supported. Error: ${e.message}")
                        }
                    }
                    else -> throw Exception("File type not supported")
                }
            }
            copy(archiveInputStream, outStream) {
                updateProgress(inputFileSize, countingInputStream.count)
            }
        } finally {
            progressJob.cancel()
            closeQuietly(inStream)
            closeQuietly(outStream)
        }
        val fileLength = storageManager.getFilesizeFromUri(finalFile.uri)
        return Pair(finalFile.uri, fileLength)
    }

    private fun closeQuietly(stream: Closeable?) {
        if (stream == null) return
        try {
            stream.close()
        } catch (e: IOException) {
            Log.w("FileUnPacker", "Failed to close stream: ${stream.javaClass.simpleName}", e)
        }
    }

    private fun updateProgress(fileSize: Long, readed: Long) {
        val percent: Float = if (fileSize > 0) readed.toFloat() / fileSize.toFloat() else 0f
        onProgressChange(percent.coerceIn(0f, 1f))
    }
}