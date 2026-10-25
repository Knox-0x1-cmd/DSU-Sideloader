package vegabobo.dsusideloader.preparation

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Job
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import vegabobo.dsusideloader.core.StorageManager

class FileUnPacker(
    private val storageManager: StorageManager,
    private val inputFile: Uri,
    outputFile: String,
    private val installationJob: Job,
    private val onProgressChange: (Float) -> Unit,
) {

    private var finalFile: DocumentFile = storageManager.createDocumentFile(outputFile)

    private val inputFileSize = storageManager.getFilesizeFromUri(inputFile)

    private fun copy(
        inputStr: InputStream,
        outputStr: OutputStream,
        onReadedBuffer: (Long) -> Unit,
    ) {
        inputStr.use { input ->
            outputStr.use { output ->
                val buffer = ByteArray(8 * 1024)
                var readed = 0L
                while (!installationJob.isCancelled) {
                    val n = input.read(buffer)
                    if (n < 0) break
                    if (n == 0) continue
                    readed += n
                    onReadedBuffer(readed)
                    output.write(buffer, 0, n)
                }
                output.flush()
            }
        }
    }

    fun pack(): Pair<Uri, Long> {
        val input = storageManager.openInputStream(inputFile)
        val compressed = GzipCompressorOutputStream(storageManager.openOutputStream(finalFile.uri))
        copy(input, compressed) { updateProgress(inputFileSize, it) }
        val fileLength = storageManager.getFilesizeFromUri(finalFile.uri)
        return Pair(finalFile.uri, fileLength)
    }

    fun unpack(): Pair<Uri, Long> {
        val archiveInputStream =
            with(storageManager.getFilenameFromUri(inputFile)) {
                when {
                    endsWith("xz") -> XZCompressorInputStream(storageManager.openInputStream(inputFile))
                    endsWith("gz") -> GzipCompressorInputStream(storageManager.openInputStream(inputFile))
                    endsWith("gzip") -> GzipCompressorInputStream(storageManager.openInputStream(inputFile))
                    else -> throw Exception("File type not supported")
                }
            }
        copy(archiveInputStream, storageManager.openOutputStream(finalFile.uri)) {
            updateProgress(inputFileSize, archiveInputStream.compressedCount)
        }
        val fileLength = storageManager.getFilesizeFromUri(finalFile.uri)
        return Pair(finalFile.uri, fileLength)
    }

    private fun updateProgress(fileSize: Long, readed: Long) {
        if (fileSize <= 0L) return
        val percent = (readed.toDouble() / fileSize.toDouble()).toFloat().coerceIn(0f, 1f)
        onProgressChange(percent)
    }
}
