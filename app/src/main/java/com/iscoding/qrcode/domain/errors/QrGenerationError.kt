package com.iscoding.qrcode.domain.errors

sealed class QrGenerationError(
    override val message: String,
    override val cause: Throwable? = null,
) : AppError {
    data class InvalidData(val reason: String) : QrGenerationError(reason)
    data class GenerationFailed(val error: Throwable) :
        QrGenerationError("QR generation failed", error)
}
