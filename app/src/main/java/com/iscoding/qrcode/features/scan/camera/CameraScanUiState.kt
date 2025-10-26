package com.iscoding.qrcode.features.scan.camera

import androidx.compose.ui.geometry.Offset

/**
 * Represents the UI state for the camera QR code scanning screen.
 *
 * This state is used in a unidirectional data flow (MVI) architecture to reflect:
 * 1. Camera permission status
 * 2. Dialog visibility (permission or URL confirmation)
 * 3. Scanned QR code data
 * 4. Tap-to-focus position on the camera preview
 *
 * @property hasCamPermission Whether the app currently has camera permission.
 * @property shouldPermissionDialog Whether to show the camera permission dialog.
 * @property shouldURLDialog Whether to show the dialog for opening a scanned URL.
 * @property shouldLaunchAppSettings Whether the user should be directed to app settings
 *                                     due to permanent permission denial.
 * @property scannedData The latest scanned QR code data (any text).
 * @property scannedUrl The latest scanned QR code data if it's a URL.
 * @property isGoodUrlRegex True if the scanned data matches a URL pattern.
 * @property tapPosition The last tap position on the camera preview for focus/metering overlays.
 */
data class CameraScanUiState(
    val hasCamPermission: Boolean = false,
    val shouldPermissionDialog: Boolean = false,
    val shouldURLDialog: Boolean = false,
    val shouldLaunchAppSettings: Boolean = false,
    val scannedData: String = "",
    val scannedUrl: String = "",
    val isGoodUrlRegex: Boolean = false,
    var tapPosition: Offset? = null,
)
