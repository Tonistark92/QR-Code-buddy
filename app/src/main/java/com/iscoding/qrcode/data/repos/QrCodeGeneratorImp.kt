package com.iscoding.qrcode.data.repos

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import arrow.core.Either
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.iscoding.qrcode.data.errors.mappers.toQrGenerationError
import com.iscoding.qrcode.domain.errors.QrGenerationError
import com.iscoding.qrcode.domain.repos.QrCodeGenerator
import com.iscoding.qrcode.domain.util.runCatchingError
import java.util.Hashtable

/**
 * Implementation of [QrCodeGenerator] using ZXing library.
 *
 * Generates a QR code bitmap from a string input with customizable width and height.
 */
class QrCodeGeneratorImp : QrCodeGenerator {

    /**
     * Generates a QR code image from the provided data string.
     *
     * @param data The text content to encode in the QR code.
     * @param width Desired width of the QR code bitmap in pixels.
     * @param height Desired height of the QR code bitmap in pixels.
     * @return A [Bitmap] representing the generated QR code.
     *
     * Example usage:
     * ```
     * val qrBitmap = QrCodeGeneratorImp().generate("https://example.com", 300, 300)
     * imageView.setImageBitmap(qrBitmap)
     * ```
     */
    override suspend fun generate(
        data: String,
        width: Int,
        height: Int,
    ): Either<QrGenerationError, Bitmap> = runCatchingError(
        mapError = { e -> e.toQrGenerationError() },
        block = {
            require(data.isNotBlank()) { "Data string cannot be empty." }

            val writer = QRCodeWriter()
            val hints = Hashtable<EncodeHintType, Any>().apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }

            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints)
            val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp[x, y] = if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
                }
            }

            bmp // this is returned as Either.Right(bmp)
        },
    )
}
