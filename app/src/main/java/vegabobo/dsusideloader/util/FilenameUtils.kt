package vegabobo.dsusideloader.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile

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
            val path = uri.path ?: return if (addQuotes) "''" else ""
            
            if (!path.contains("/document/")) {
                return if (addQuotes) "'${uri.toString()}'" else uri.toString()
            }
            
            val parts = path.split("/document/")
            if (parts.size < 2) {
                return if (addQuotes) "'${uri.toString()}'" else uri.toString()
            }
            
            val safStorage = parts[1].replace("/tree/", "")
            val pathParts = safStorage.split(":")
            if (pathParts.size < 2) {
                return if (addQuotes) "'${uri.toString()}'" else uri.toString()
            }
            
            val docPath = pathParts[1]
            return when {
                docPath.contains("/storage/emulated") -> {
                    val result = "file://$docPath"
                    if (addQuotes) "'$result'" else result
                }
                safStorage.contains("primary") -> {
                    val storagePath = "file:///storage/emulated/0/"
                    val finalPath = "$storagePath$docPath"
                    if (addQuotes) "'$finalPath'" else finalPath
                }
                else -> {
                    val storagePath = "file:///storage/"
                    val finalPath = storagePath + safStorage.replace(":", "/")
                    if (addQuotes) "'$finalPath'" else finalPath
                }
            }
        }

        fun queryName(resolver: ContentResolver, uri: Uri): String {
            val cursor = resolver.query(uri, null, null, null, null)
            if (cursor == null) {
                return uri.lastPathSegment ?: uri.toString()
            }
            try {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex < 0 || !cursor.moveToFirst()) {
                    return uri.lastPathSegment ?: uri.toString()
                }
                return cursor.getString(nameIndex)
            } finally {
                cursor.close()
            }
        }

        fun getDigits(input: String): String {
            return appendToDigitsToString(input, "")
        }

        fun getLengthFromFile(context: Context, uri: Uri): Long {
            return DocumentFile.fromSingleUri(context, uri)?.length() ?: -1
        }
    }
}