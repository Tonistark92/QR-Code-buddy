package com.iscoding.qrcode.features.mainactivity.intent

import android.net.Uri

/**
 * Represents one-time effects (side effects) that the [MainActivity] should perform.
 *
 * Unlike UI state, which is persistent, these effects are consumed once and not replayed.
 * Examples: navigation, showing toasts, or starting an analysis process.
 */
sealed class MainActivityEffect {

    /**
     * Displays a short message to the user (toast or snackbar).
     *
     * @param message The text to display.
     */
    data class ShowToast(val message: String) : MainActivityEffect()

    /**
     * Navigates to the QR code details screen.
     *
     * @param qrCode The decoded QR code text.
     * @param imageUri The URI of the QR code image (from storage or camera).
     */
    data class NavigateToQrDetailsScreen(
        val qrCode: String,
        val imageUri: Uri,
    ) : MainActivityEffect()

    /**
     * Triggers the QR code analysis process (e.g., launching an analyzer).
     */
    object AnalyzeImage : MainActivityEffect()
}
