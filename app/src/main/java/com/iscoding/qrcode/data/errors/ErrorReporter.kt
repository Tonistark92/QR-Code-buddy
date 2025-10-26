package com.iscoding.qrcode.data.errors

import com.iscoding.qrcode.domain.errors.AppError
import com.iscoding.qrcode.domain.errors.MediaError
import com.iscoding.qrcode.domain.errors.QrAnalyzerError
import com.iscoding.qrcode.domain.errors.QrGenerationError
import com.iscoding.qrcode.domain.errors.QrScannerError

object ErrorReporter {

    fun log(error: AppError, map: Map<String, Any>? = null) {
        when (error) {
            is QrScannerError -> handleQrScannerError(error, map)
            is QrGenerationError -> handleQrGenerationError(error, map)
            is QrAnalyzerError -> handleQrAnalyzerError(error, map)
            is MediaError -> handleMediaError(error, map)
        }
    }

    private fun handleQrScannerError(error: QrScannerError, map: Map<String, Any>?) {
        when (error) {
            QrScannerError.PermissionDenied,
            QrScannerError.NoQrFound,
            is QrScannerError.InvalidQrContent,
            -> Unit
            else -> captureError(error, map)
        }
    }

    private fun handleQrGenerationError(error: QrGenerationError, map: Map<String, Any>?) {
        when (error) {
            is QrGenerationError.InvalidData -> Unit
            is QrGenerationError.GenerationFailed -> captureError(error, map)
        }
    }

    private fun handleQrAnalyzerError(error: QrAnalyzerError, map: Map<String, Any>?) {
        when (error) {
            QrAnalyzerError.NoQrFound -> Unit
            is QrAnalyzerError.DecodingFailed -> captureError(error, map)
            else -> captureError(error, map)
        }
    }

    private fun handleMediaError(error: MediaError, map: Map<String, Any>?) {
        when (error) {
            MediaError.PermissionDenied,
            MediaError.EmptyResult,
            -> Unit
            is MediaError.QueryFailed -> captureError(error, map)
            else -> captureError(error, map)
        }
    }

    private fun captureError(error: AppError, map: Map<String, Any>?) {
//        val sentryScope = Sentry.withScope { scope ->
//            map?.forEach { (key, value) ->
//                scope.setExtra(key, value.toString())
//            }
//            when (error) {
//                is Throwable -> Sentry.captureException(error)
////                else -> Sentry.captureMessage(error.message)
//            }
//        }
    }
}
