package com.tt.muzien.utilities

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


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
    fun Fragment.closeKeyboard(context: Context) {
        val view = requireActivity().currentFocus ?: View(requireContext())
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun capitalizeFirstWord(variable: String): String {
        return variable.replaceFirstChar { it.uppercase() }
    }
    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // Earth radius in meters

        val phi1 = lat1 * (Math.PI / 180) // Convert latitude to radians
        val phi2 = lat2 * (Math.PI / 180) // Convert latitude to radians
        val deltaPhi = (lat2 - lat1) * (Math.PI / 180) // Difference in latitude in radians
        val deltaLambda = (lon2 - lon1) * (Math.PI / 180) // Difference in longitude in radians

        val a = sin(deltaPhi / 2).pow(2.0) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

}