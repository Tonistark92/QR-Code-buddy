package com.iscoding.qrcode.data.repos

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.iscoding.qrcode.data.errors.ErrorReporter
import com.iscoding.qrcode.domain.errors.QrScannerError
import com.iscoding.qrcode.domain.repos.QrCodeScanner
import com.iscoding.qrcode.domain.util.runBlockingError
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

            private val supportedFormats = listOf(
                ImageFormat.YUV_420_888,
                ImageFormat.YUV_422_888,
                ImageFormat.YUV_444_888,
            )

            override fun analyze(image: ImageProxy) {
                runBlockingError(
                    block = {
                        if (image.format !in supportedFormats) {
                            ErrorReporter.log(QrScannerError.InvalidQrContent("Unsupported image format"))
                            image.close()
                            return@runBlockingError
                        }
                        val bytes = image.planes.first().buffer.toByteArray()
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

                        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                        val reader = MultiFormatReader().apply {
                            setHints(
                                mapOf(
                                    DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                                ),
                            )
                        }

                        val result = reader.decode(binaryBitmap)
                        onResult(result.text)
                    },
                    mapError = { e ->
                        when (e) {
                            is NotFoundException -> QrScannerError.NoQrFound
                            is IllegalArgumentException -> QrScannerError.InvalidQrContent(e.message ?: "Invalid content")
                            else -> QrScannerError.Unknown(e)
                        }
                    },
                )

                image.close() // Always close
            }

            private fun ByteBuffer.toByteArray(): ByteArray {
                rewind()
                return ByteArray(remaining()).also { get(it) }
            }
        }
    }
}
