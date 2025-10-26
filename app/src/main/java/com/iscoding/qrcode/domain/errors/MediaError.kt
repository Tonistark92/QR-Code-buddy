package com.iscoding.qrcode.domain.errors

sealed class MediaError(
    override val message: String,
    override val cause: Throwable? = null,
) : AppError {
    object PermissionDenied : MediaError("Storage permission denied")
    object EmptyResult : MediaError("No media found")
    data class QueryFailed(val reason: String) : MediaError(reason)
    object Unknown : MediaError("Unknown media error")
}
