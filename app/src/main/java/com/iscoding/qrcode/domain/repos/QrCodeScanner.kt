package com.iscoding.qrcode.domain.repos

import androidx.camera.core.ImageAnalysis

/**
 * Repository interface for scanning QR codes from live camera input.
 *
 * Provides a method to get an [ImageAnalysis.Analyzer] that can be attached
 * to a CameraX [ImageAnalysis] use case. The analyzer will detect QR codes
 * in real-time and pass the result to a callback.
 */
interface QrCodeScanner {

    /**
     * Returns a CameraX [ImageAnalysis.Analyzer] configured to detect QR codes.
     *
     * @param onResult Lambda that is invoked whenever a QR code is successfully scanned.
     *                 The scanned QR code content is provided as a [String].
     * @return An instance of [ImageAnalysis.Analyzer] to attach to CameraX.
     *
     * Example usage:
     * ```
     * val analyzer = qrCodeScanner.getAnalyzer { qrContent ->
     *     println("QR Code detected: $qrContent")
     * }
     * val imageAnalysis = ImageAnalysis.Builder().build().also {
     *     it.setAnalyzer(executor, analyzer)
     * }
     * ```
     */
    fun getAnalyzer(onResult: (String) -> Unit): ImageAnalysis.Analyzer
}
