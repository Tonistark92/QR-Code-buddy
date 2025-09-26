package com.iscoding.qrcode.data.repos

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.iscoding.qrcode.domain.repos.QrCodeScanner
import java.nio.ByteBuffer

/**
 * Implementation of [QrCodeScanner] using CameraX and ZXing.
 *
 * Provides a CameraX [ImageAnalysis.Analyzer] that detects QR codes in real-time.
 */
class QrCodeScannerImp : QrCodeScanner {

    /**
     * Returns an [ImageAnalysis.Analyzer] to attach to CameraX for QR code scanning.
     *
     * @param onResult Callback invoked when a QR code is successfully detected.
     * @return An [ImageAnalysis.Analyzer] instance.
     *
     * Example usage:
     * ```
     * val analyzer = qrCodeScanner.getAnalyzer { qrContent ->
     *     println("QR Code detected: $qrContent")
     * }
     * imageAnalysis.setAnalyzer(executor, analyzer)
     * ```
     */
    override fun getAnalyzer(onResult: (String) -> Unit): ImageAnalysis.Analyzer {
        return object : ImageAnalysis.Analyzer {

            // Supported camera image formats
            private val supportedImageFormats = listOf(
                ImageFormat.YUV_420_888,
                ImageFormat.YUV_422_888,
                ImageFormat.YUV_444_888,
            )

            override fun analyze(image: ImageProxy) {
                if (image.format in supportedImageFormats) {
                    val bytes = image.planes.first().buffer.toByteArray()

                    // Convert camera image to a binary bitmap for ZXing
                    val source = PlanarYUVLuminanceSource(
                        bytes,
                        image.width,
                        image.height,
                        0,
                        0,
                        image.width,
                        image.height,
                        false,
                    )

                    val binaryBmp = BinaryBitmap(HybridBinarizer(source))

                    try {
                        val result = MultiFormatReader().apply {
                            setHints(
                                mapOf(
                                    DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                        BarcodeFormat.QR_CODE,
                                    ),
                                ),
                            )
                        }.decode(binaryBmp)

                        onResult(result.text) // Pass QR code text to callback
                    } catch (e: Exception) {
                        e.printStackTrace() // No QR code found, ignore
                    } finally {
                        image.close() // Always close ImageProxy
                    }
                } else {
                    image.close() // Unsupported format
                }
            }

            /**
             * Converts a [ByteBuffer] to a [ByteArray].
             */
            private fun ByteBuffer.toByteArray(): ByteArray {
                rewind()
                return ByteArray(remaining()).also { get(it) }
            }
        }
    }
}
