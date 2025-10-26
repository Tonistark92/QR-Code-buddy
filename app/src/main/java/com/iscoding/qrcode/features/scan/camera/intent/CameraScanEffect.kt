package com.iscoding.qrcode.features.scan.camera.intent

/**
 * Represents one-time effects or side-effects in the Camera Scan feature.
 *
 * These are events that the UI should react to only once, such as navigation,
 * showing a toast, or requesting permissions.
 */
sealed class CameraScanEffect {

    /** Opens a URL detected in a QR code. */
    object OpenUrl : CameraScanEffect()

    /**
     * Shows a short toast message to the user.
     * @param message The message to display.
     */
    data class ShowToast(val message: String) : CameraScanEffect()

    /**
     * Copies detected data to the system clipboard.
     * @param data The text to copy.
     */
    data class CopyToTheClipBoard(val data: String) : CameraScanEffect()

    /** Requests camera permission from the user. */
    object RequestCameraPermission : CameraScanEffect()

    /** Opens the app settings so the user can grant permissions manually. */
    object OpenAppSettings : CameraScanEffect()
}
