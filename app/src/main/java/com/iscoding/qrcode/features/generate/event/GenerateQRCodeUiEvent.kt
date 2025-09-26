package com.iscoding.qrcode.features.generate.event

import android.graphics.Bitmap

/**
 * Represents one-time UI events (side effects) that occur in the "Generate QR Code" feature.
 *
 * Unlike [GenerateQrEvent], which models user input and interactions,
 * these events are meant to be consumed by the UI layer (e.g., Activity/Compose).
 *
 * Typical use-cases:
 * - Navigation
 * - Showing toasts/snackbars
 * - Triggering Android system actions (e.g., sharing)
 */
sealed class GenerateQRCodeUiEvent {

    /**
     * Requests the system share sheet to share the generated QR code as an image.
     *
     * @param bitmap The generated QR code image to share.
     */
    data class RequestShare(val bitmap: Bitmap) : GenerateQRCodeUiEvent()

    /**
     * Displays a short toast/snackbar message in the UI.
     *
     * @param message The message text to display.
     */
    data class ShowToast(val message: String) : GenerateQRCodeUiEvent()
}
