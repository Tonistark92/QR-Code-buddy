package com.iscoding.qrcode.data.repos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import arrow.core.Either
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.common.HybridBinarizer
import com.iscoding.qrcode.data.errors.ErrorReporter
import com.iscoding.qrcode.domain.errors.QrAnalyzerError
import com.iscoding.qrcode.domain.repos.QrCodeStorageAnalyzer
import com.iscoding.qrcode.domain.util.runBlockingError
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
        runBlockingError(
            block = {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                    ?: throw IllegalArgumentException("Failed to decode bitmap from URI: $uri")

                val binaryBitmap = createBinaryBitmap(bitmap)
                val result = MultiFormatReader().decode(binaryBitmap)
                onQrCodeScanned(result.text)
            },
            mapError = { exception ->
                when (exception) {
                    is NotFoundException -> QrAnalyzerError.NoQrFound
                    is IllegalArgumentException -> QrAnalyzerError.InvalidImage(exception.message)
                    is ReaderException -> QrAnalyzerError.DecodingFailed(exception)
                    else -> QrAnalyzerError.Unknown(exception)
                }.also { error ->
                    ErrorReporter.log(error, mapOf("uri" to uri.toString()))
                }
            },
        ).also { either ->
            when (either) {
                is Either.Left -> {
                    // Notify UI through callbacks if needed
                    when (either.value) {
                        is QrAnalyzerError.NoQrFound -> onNoQRCodeFound()
                        else -> ErrorReporter.log(either.value)
                    }
                }

                is Either.Right -> {
                    // QR scanned successfully — callback already triggered
                }
            }
        }

        inputStream.close()
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
