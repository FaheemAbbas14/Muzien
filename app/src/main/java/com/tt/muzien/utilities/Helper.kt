package com.tt.muzien.utilities

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by Faheem Abbas on 01/01/2025.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
object Helper {
    fun dpToPx(context: Context,dpValue: Int): Int {
        return (dpValue * context.resources.displayMetrics.density).toInt()
    }
    fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver: ContentResolver = context.contentResolver
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg" // Change extension as needed
        val tempFile = File(context.cacheDir, fileName)

        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    fun getFileExtension(context: Context, uri: Uri): String? {
        var extension: String? = null

        // Query content resolver to get file name
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                cursor.moveToFirst()
                val fileName = cursor.getString(nameIndex)
                extension = fileName.substringAfterLast('.', "")
            }
        }

        return extension
    }
}