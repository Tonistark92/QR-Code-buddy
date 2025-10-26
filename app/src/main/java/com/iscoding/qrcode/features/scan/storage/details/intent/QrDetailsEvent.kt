package com.iscoding.qrcode.features.scan.storage.details.intent

/**
 * Represents all possible user or system events for the QR Details screen.
 * These events are sent from the UI to the ViewModel to trigger state changes or effects.
 */
sealed class QrDetailsEvent {

    /**
     * Triggered when the QR Details screen is first opened.
     *
     * @param qrCodeData The data extracted from the QR code.
     * @param imageUri The URI of the image that contained the QR code.
     */
    data class OnInitialDetailsScreen(
        val qrCodeData: String,
        val imageUri: String,
    ) : QrDetailsEvent()

    /**
     * Triggered when the user requests to open the URL contained in the QR code data.
     */
    object OnOpenUrl : QrDetailsEvent()

    /**
     * Triggered when the user long-presses the QR code data on the screen.
     * Typically used to copy the QR data to the clipboard.
     */
    object OnQrDataLongPressed : QrDetailsEvent()
}
