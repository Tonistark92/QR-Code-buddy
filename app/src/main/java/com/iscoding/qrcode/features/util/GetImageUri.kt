package com.iscoding.qrcode.features.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Converts a [Bitmap] into a sharable [Uri] using a FileProvider.
 *
 * This is useful for sharing generated QR code images or other bitmaps
 * between apps or passing them via Intents.
 *
 * @param context The context used to access cache directory and FileProvider.
 * @param bitmap The bitmap image to convert to a Uri.
 *
 * @return A [Uri] pointing to the saved image file, or null if an error occurs.
 *
 * Usage example:
 * ```
 * val bitmapUri = getImageUri(context, bitmap)
 * if (bitmapUri != null) {
 *     val shareIntent = Intent().apply {
 *         action = Intent.ACTION_SEND
 *         putExtra(Intent.EXTRA_STREAM, bitmapUri)
 *         type = "image/png"
 *     }
 *     context.startActivity(Intent.createChooser(shareIntent, "Share QR code"))
 * }
 * ```
 */
fun getImageUri(
    context: Context,
    bitmap: Bitmap,
): Uri? {
    val imagesFolder = File(context.cacheDir, "images")
    var uri: Uri? = null
    try {
        // Create the directory if it doesn't exist
        imagesFolder.mkdirs()

        // Create a temporary image file
        val file = File(imagesFolder, "shared_image.png")

        // Save bitmap to the file as PNG with 90% quality
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        stream.flush()
        stream.close()

        // Get a content URI using FileProvider
        uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    } catch (e: Exception) {
        e.printStackTrace() // Log error if something goes wrong
    }
    return uri
}
