package com.iscoding.qrcode.features.scan.storage.details.intent

/**
 * Represents one-time effects for the QR Details screen.
 * These are actions that the UI should handle but are not part of persistent state,
 * such as showing a toast, navigating to a URL, or handling a long-press.
 */
sealed class QrDetailsEffect {

    /**
     * Show a short message to the user.
     *
     * @param message The message to display in a toast.
     */
    data class ShowToast(val message: String) : QrDetailsEffect()

    /**
     * Triggered when the user long-presses on the QR data.
     * Can be used to copy the QR data to the clipboard or show a context menu.
     *
     * @param QrData The QR code data string that was long-pressed.
     */
    data class OnQrDataLongPressed(val QrData: String) : QrDetailsEffect()

    /**
     * Triggered when the user wants to open a URL from the QR data.
     *
     * @param urlString The URL string to be opened in a browser or WebView.
     */
    data class OnOpenUrl(val urlString: String) : QrDetailsEffect()
}
