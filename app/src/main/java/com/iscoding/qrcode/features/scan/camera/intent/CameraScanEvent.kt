package com.iscoding.qrcode.features.scan.camera.intent

import androidx.compose.ui.geometry.Offset

/**
 * Represents all possible events (user actions or system events) in the Camera Scan feature.
 *
 * These events are sent from the UI to the ViewModel to trigger state updates or side effects.
 */
sealed class CameraScanEvent {

    /**
     * Triggered when the screen performs its initial permission check.
     * @param hasPermission True if camera permission is already granted.
     */
    data class OnInitialPermissionCheck(val hasPermission: Boolean) : CameraScanEvent()

    /**
     * Triggered when the user taps on the camera preview to focus.
     * @param offset The tap location in screen coordinates.
     */
    data class OnTapToFocus(val offset: Offset) : CameraScanEvent()

    /** Clears any active tap-to-focus indicator. */
    object OnClearTapToFocus : CameraScanEvent()

    /**
     * Triggered when the camera permission request result is received.
     * @param granted True if the permission was granted.
     * @param shouldShowRationale True if the user denied the permission but did not select "Don't ask again".
     */
    data class OnCameraPermissionResult(
        val granted: Boolean,
        val shouldShowRationale: Boolean,
    ) : CameraScanEvent()

    /** Triggered when the camera permission status changes (granted/revoked). */
    data class OnPermissionStatusChanged(val hasPermission: Boolean) : CameraScanEvent()

    /** Validates whether the scanned QR code data is a URL. */
    object OnValidateRegexForUrl : CameraScanEvent()

    /** Opens the URL from the scanned QR code, if valid. */
    object OnOpenUrlFromScannedQrCode : CameraScanEvent()

    /** Triggered when a QR code is successfully scanned. */
    data class OnScannedQrCode(val data: String) : CameraScanEvent()

    /** Requests camera permission from the user. */
    object OnRequestCameraPermission : CameraScanEvent()

    /** Triggered when the user long-presses on the scanned text. */
    object OnTextLongPressed : CameraScanEvent()

    /** Opens the app settings to allow the user to manually grant permissions. */
    object OnOpenAppSettings : CameraScanEvent()

    /** Dismisses the permission dialog. */
    object OnDismissPermissionDialog : CameraScanEvent()

    /** Dismisses the URL confirmation dialog. */
    object OnDismissUrlDialog : CameraScanEvent()
}
