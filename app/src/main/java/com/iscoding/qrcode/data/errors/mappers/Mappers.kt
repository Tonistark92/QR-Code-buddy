package com.iscoding.qrcode.data.errors.mappers

import android.graphics.ImageDecoder
import android.hardware.camera2.CameraAccessException
import android.os.Build
import androidx.annotation.RequiresApi
import com.iscoding.qrcode.domain.errors.MediaError
import com.iscoding.qrcode.domain.errors.QrAnalyzerError
import com.iscoding.qrcode.domain.errors.QrGenerationError
import com.iscoding.qrcode.domain.errors.QrScannerError
import java.io.IOException

fun Exception.toMediaError(): MediaError = when (this) {
    is SecurityException -> MediaError.PermissionDenied
    is IOException -> MediaError.QueryFailed(message ?: "I/O error")
    else -> MediaError.Unknown
}

@RequiresApi(Build.VERSION_CODES.P)
fun Exception.toQrAnalyzerError(): QrAnalyzerError = when (this) {
    is IllegalArgumentException -> QrAnalyzerError.InvalidImage()
    is ImageDecoder.DecodeException -> QrAnalyzerError.DecodingFailed(this)
    else -> QrAnalyzerError.NoQrFound
}

fun Exception.toQrGenerationError(): QrGenerationError = when (this) {
    is IllegalStateException -> QrGenerationError.InvalidData(message ?: "Invalid input")
    else -> QrGenerationError.GenerationFailed(this)
}

@RequiresApi(Build.VERSION_CODES.P)
fun Exception.toQrScannerError(): QrScannerError = when (this) {
    is SecurityException -> QrScannerError.PermissionDenied
    is CameraAccessException -> QrScannerError.CameraUnavailable
    is ImageDecoder.DecodeException -> QrScannerError.DecodingFailed
    else -> QrScannerError.Unknown(this)
}
