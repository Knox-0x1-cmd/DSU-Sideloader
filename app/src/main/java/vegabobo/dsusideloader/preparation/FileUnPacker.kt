package vegabobo.dsusideloader.preparation

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.FilterInputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlinx.coroutines.Job
import org.tukaani.xz.XZInputStream
import vegabobo.dsusideloader.core.StorageManager

class FileUnPacker(
    private val storageManager: StorageManager,
    private val inputFile: Uri,
    outputFile: String,
    private val installationJob: Job,
    private val onProgressChange: (Float) -> Unit,
) {

    private var finalFile: DocumentFile = storageManager.createDocumentFile(outputFile)

    private var outputStream = storageManager.openOutputStream(finalFile.uri)
    private var inputStream = storageManager.openInputStream(inputFile)
    private val inputFileSize = storageManager.getFilesizeFromUri(inputFile)

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
        while (-1 != inputStr.read(buffer)
                .also { n = it } && !installationJob.isCancelled
        ) {
            readed += n
            onReadedBuffer(readed)
            outputStr.write(buffer, 0, n)
        }
        inputStr.close()
        outputStr.flush()
        outputStr.close()
    }

    fun pack(): Pair<Uri, Long> {
        copy(inputStream, GZIPOutputStream(outputStream)) {
            updateProgress(inputFileSize, it)
        }
        val fileLength = storageManager.getFilesizeFromUri(finalFile.uri)
        return Pair(finalFile.uri, fileLength)
    }

    fun unpack(): Pair<Uri, Long> {
        val countingInputStream = CountingInputStream(inputStream)
        val archiveInputStream =
            with(storageManager.getFilenameFromUri(inputFile)) {
                when {
                    endsWith("xz") -> XZInputStream(countingInputStream)
                    endsWith("gz") -> GZIPInputStream(countingInputStream)
                    endsWith("gzip") -> GZIPInputStream(countingInputStream)
                    else -> throw Exception("File type not supported")
                }
            }
        copy(archiveInputStream, outputStream) {
            updateProgress(inputFileSize, countingInputStream.count)
        }
        val fileLength = storageManager.getFilesizeFromUri(finalFile.uri)
        return Pair(finalFile.uri, fileLength)
    }

    private fun updateProgress(fileSize: Long, readed: Long) {
        val percent: Float = readed.toFloat() / fileSize.toFloat()
        onProgressChange(percent)
    }
}
