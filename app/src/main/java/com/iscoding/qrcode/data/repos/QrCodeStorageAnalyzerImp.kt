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

class QrCodeStorageAnalyzerImp : QrCodeStorageAnalyzer {

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
                    onQrCodeScanned(qrCodeText)
                } catch (e: NotFoundException) {
                    // Handle case where no QR code is found
                    onNoQRCodeFound()
                }
            } else {
                logcat { "Failed to load Bitmap from URI: $uri" }
            }
        } catch (e: Exception) {
            logcat { "Error analyzing QR code: ${e.message}" }
            logcat { e.asLog() }
        } finally {
            inputStream.close()
        }
    }

    private fun createBinaryBitmap(bitmap: Bitmap): BinaryBitmap {
        val luminanceSource = createRGBLuminanceSource(bitmap)
        return BinaryBitmap(HybridBinarizer(luminanceSource))
    }

    private fun createRGBLuminanceSource(bitmap: Bitmap): RGBLuminanceSource {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return RGBLuminanceSource(width, height, pixels)
    }
}
