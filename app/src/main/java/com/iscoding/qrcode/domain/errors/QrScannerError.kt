package com.iscoding.qrcode.domain.errors

sealed class QrScannerError(
    override val message: String,
    override val cause: Throwable? = null,
) : AppError {
    object PermissionDenied : QrScannerError("Camera permission denied")
    object CameraUnavailable : QrScannerError("Camera unavailable")
    object DecodingFailed : QrScannerError("Failed to decode QR frame")
    object NoQrFound : QrScannerError("No QR code found")
    data class InvalidQrContent(val reason: String) : QrScannerError(reason)
    data class Unknown(val error: Throwable? = null) :
        QrScannerError("Unknown scanner error", error)
}
