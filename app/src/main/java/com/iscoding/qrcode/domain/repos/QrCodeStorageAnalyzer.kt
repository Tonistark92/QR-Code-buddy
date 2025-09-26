package com.iscoding.qrcode.domain.repos

import android.net.Uri
import java.io.InputStream

/**
 * Repository interface for analyzing QR codes from images stored in the device.
 *
 * Provides a method to detect QR codes in an image file, either from a content [Uri]
 * or an [InputStream]. The result is returned via callbacks.
 */
interface QrCodeStorageAnalyzer {

    /**
     * Analyzes the given image for QR codes.
     *
     * @param uri The [Uri] pointing to the image to analyze. Useful for identifying the image source.
     * @param inputStream The [InputStream] of the image file to scan.
     * @param onNoQRCodeFound Callback invoked if no QR code is detected in the image or an error occurs.
     * @param onQrCodeScanned Callback invoked when a QR code is successfully detected.
     *                        Provides the scanned QR code content as a [String].
     *
     * Example usage:
     * ```
     * qrCodeStorageAnalyzer.analyze(uri, inputStream,
     *     onNoQRCodeFound = { println("No QR code found") },
     *     onQrCodeScanned = { qrContent -> println("QR Code: $qrContent") }
     * )
     * ```
     */
    fun analyze(
        uri: Uri,
        inputStream: InputStream,
        onNoQRCodeFound: () -> Unit,
        onQrCodeScanned: (String) -> Unit,
    )
}
