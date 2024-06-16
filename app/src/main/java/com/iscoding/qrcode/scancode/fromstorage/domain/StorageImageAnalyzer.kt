package com.iscoding.qrcode.scancode.fromstorage.domain
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.io.InputStream

class StorageImageAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) {

    fun analyze(uri: Uri, inputStream: InputStream) {
        try {
            // Load bitmap from URI using BitmapFactory
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

            if (bitmap != null) {
                // Convert Bitmap to BinaryBitmap for ZXing library
                val binaryBitmap = createBinaryBitmap(bitmap)

                // Use ZXing MultiFormatReader to decode QR code
                val result = MultiFormatReader().decode(binaryBitmap)

                // Handle QR code result
                val qrCodeText = result.text
                Log.d("StorageImageAnalyzer", "Scanned QR Code: $qrCodeText")
                onQrCodeScanned(qrCodeText)
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