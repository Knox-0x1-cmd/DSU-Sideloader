package vegabobo.dsusideloader.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import java.io.File

class FilenameUtils {

    companion object {

        /**
         * Append text to the end of all digits containing in a string
         * @param input String containing digits
         * @param textToAppend Text that will be appended
         * @return Formatted string, if there is no digits in "input", a empty string will be returned.
         */
        fun appendToDigitsToString(input: String, textToAppend: String): String {
            var newText = input.filter { it.isDigit() } + textToAppend
            if (newText == textToAppend) {
                newText = ""
            }
            return newText
        }

        /**
         * Tries to convert DocumentFile uri to real path
         * isn't guaranteed that will work with all kinds of path
         */
        fun getFilePath(uri: Uri, addQuotes: Boolean = false): String {
            val path = when {
                uri.scheme == "file" -> uri.path ?: throw IllegalArgumentException("File URI has no path")
                uri.authority == "com.android.externalstorage.documents" -> {
                    val documentId = DocumentsContract.getDocumentId(uri)
                    val separator = documentId.indexOf(':')
                    require(separator > 0) { "Unsupported external-storage document URI" }
                    val volume = documentId.substring(0, separator)
                    val relative = documentId.substring(separator + 1).trimStart('/')
                    require(volume.matches(Regex("[A-Za-z0-9._-]+")) && volume != "." && volume != "..") {
                        "Invalid storage volume id"
                    }
                    require(relative.split('/').none { it == ".." }) { "Invalid storage document path" }
                    val base = if (volume.equals("primary", ignoreCase = true)) {
                        File("/storage/emulated/0")
                    } else {
                        File("/storage", volume)
                    }.canonicalFile
                    val resolved = File(base, relative).canonicalFile
                    require(resolved == base || resolved.toPath().startsWith(base.toPath())) {
                        "Document URI resolves outside its storage volume"
                    }
                    resolved.path
                }
                else -> throw IllegalArgumentException("URI is not backed by a filesystem path")
            }
            val fileUri = Uri.fromFile(File(path)).toString()
            return if (addQuotes) "'${fileUri.replace("'", "'\\''")}'" else fileUri
        }

        fun queryName(resolver: ContentResolver, uri: Uri): String {
            val name = resolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
            }
            return name?.takeIf { it.isNotBlank() }
                ?: uri.lastPathSegment?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
                ?: "unnamed"
        }

        fun getDigits(input: String): String {
            return appendToDigitsToString(input, "")
        }

        fun getLengthFromFile(context: Context, uri: Uri): Long {
            val documentLength = DocumentFile.fromSingleUri(context, uri)?.length() ?: -1L
            if (documentLength > 0L) return documentLength
            return context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex >= 0 && cursor.moveToFirst()) cursor.getLong(sizeIndex) else -1L
            } ?: -1L
        }
    }
}
