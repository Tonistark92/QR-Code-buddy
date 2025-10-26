package com.iscoding.qrcode.domain.errors

sealed class QrAnalyzerError(
    override val message: String,
    override val cause: Throwable? = null,
) : AppError {

    /** No QR code was found in the image. */
    object NoQrFound : QrAnalyzerError("No QR code detected")

    /** Failed to decode the image data into a QR code. */
    data class DecodingFailed(val error: Throwable) :
        QrAnalyzerError("Failed to decode QR", error)

    /** The image was invalid or unreadable (e.g. BitmapFactory returned null). */
    data class InvalidImage(val reason: String? = null) :
        QrAnalyzerError("Invalid image content${reason?.let { ": $it" } ?: ""}")

    /** Fallback for any unexpected error in the analyzer pipeline. */
    data class Unknown(val error: Throwable? = null) :
        QrAnalyzerError("Unknown analyzer error", error)
}
