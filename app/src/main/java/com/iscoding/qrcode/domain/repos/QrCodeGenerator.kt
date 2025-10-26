package com.iscoding.qrcode.domain.repos

import android.graphics.Bitmap
import arrow.core.Either
import com.iscoding.qrcode.domain.errors.QrGenerationError

/**
 * Repository interface for generating QR codes.
 *
 * Provides a function to generate a QR code as a [Bitmap] from a given string.
 * This interface abstracts the underlying QR code generation library or algorithm,
 * allowing the domain layer to remain independent of implementation details.
 */
interface QrCodeGenerator {

    /**
     * Generates a QR code image from the given data string.
     *
     * @param data The textual content to encode in the QR code.
     * @param width The desired width of the generated QR code bitmap in pixels.
     * @param height The desired height of the generated QR code bitmap in pixels.
     * @return A [Bitmap] representing the generated QR code.
     *
     * Example usage:
     * ```
     * val qrBitmap = qrCodeGenerator.generate("https://example.com", 300, 300)
     * imageView.setImageBitmap(qrBitmap)
     * ```
     */
    suspend fun generate(
        data: String,
        width: Int,
        height: Int,
    ): Either<QrGenerationError, Bitmap>
}
