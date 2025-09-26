package com.iscoding.qrcode.data.repos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.iscoding.qrcode.domain.repos.QrCodeStorageAnalyzer
import logcat.asLog
import logcat.logcat
import java.io.InputStream

/**
 * Implementation of [QrCodeStorageAnalyzer] for analyzing images from storage.
 *
 * Converts an [InputStream] or [Uri] image into a QR code, if present.
 */
class QrCodeStorageAnalyzerImp : QrCodeStorageAnalyzer {

    /**
     * Analyzes the given [InputStream] for a QR code.
     *
     * @param uri The [Uri] of the image (for logging/debugging purposes).
     * @param inputStream The image content as an [InputStream].
     * @param onNoQRCodeFound Callback invoked if no QR code is detected.
     * @param onQrCodeScanned Callback invoked when a QR code is successfully decoded.
     */
    override fun analyze(
        uri: Uri,
        inputStream: InputStream,
        onNoQRCodeFound: () -> Unit,
        onQrCodeScanned: (String) -> Unit,
    ) {
        try {
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

            if (bitmap != null) {
                val binaryBitmap = createBinaryBitmap(bitmap)

                try {
                    val result = MultiFormatReader().decode(binaryBitmap)
                    val qrCodeText = result.text
                    onQrCodeScanned(qrCodeText) // QR code detected
                } catch (e: NotFoundException) {
                    onNoQRCodeFound() // No QR code in the image
                }
            } else {
                logcat { "Failed to load Bitmap from URI: $uri" }
            }
        } catch (e: Exception) {
            logcat { "Error analyzing QR code: ${e.message}" }
            logcat { e.asLog() }
        } finally {
            inputStream.close() // Always close InputStream to avoid leaks
        }
    }

    /**
     * Converts a [Bitmap] to ZXing's [BinaryBitmap] for decoding.
     */
    private fun createBinaryBitmap(bitmap: Bitmap): BinaryBitmap {
        val luminanceSource = createRGBLuminanceSource(bitmap)
        return BinaryBitmap(HybridBinarizer(luminanceSource))
    }

    /**
     * Creates an [RGBLuminanceSource] from a [Bitmap].
     *
     * @param bitmap The image to process.
     * @return A luminance source for ZXing decoding.
     */
    private fun createRGBLuminanceSource(bitmap: Bitmap): RGBLuminanceSource {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return RGBLuminanceSource(width, height, pixels)
    }
}
