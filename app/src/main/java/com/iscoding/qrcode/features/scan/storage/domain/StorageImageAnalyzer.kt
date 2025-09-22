package com.iscoding.qrcode.features.scan.storage.domain
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.io.InputStream

class StorageImageAnalyzer(
    private val onNoQRCodeFound: () -> Unit,
    private val onQrCodeScanned: (String) -> Unit,
) {
    fun analyze(
        uri: Uri,
        inputStream: InputStream,
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
                Log.e("StorageImageAnalyzer", "Failed to load Bitmap from URI: $uri")
            }
        } catch (e: Exception) {
            Log.e("StorageImageAnalyzer", "Error analyzing QR code: ${e.message}")
            e.printStackTrace()
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
