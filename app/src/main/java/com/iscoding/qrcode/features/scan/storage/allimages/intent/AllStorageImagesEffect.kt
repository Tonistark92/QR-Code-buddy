package com.iscoding.qrcode.features.scan.storage.allimages.intent

/**
 * Represents one-time UI effects for the "All Storage Images" screen.
 *
 * These effects are typically observed by the UI layer and include actions such as:
 * showing a toast, navigating to another screen, analyzing an image, or requesting permissions.
 */
sealed class AllStorageImagesEffect {

    /**
     * Shows a short message to the user.
     *
     * @param message The message to display in the toast.
     */
    data class ShowToast(val message: String) : AllStorageImagesEffect()

    /**
     * Navigates to the QR Code details screen with the provided data.
     *
     * @param qrCodeData The scanned QR code text.
     * @param imageUri The URI of the image that contains the QR code.
     */
    data class NavigateToQrDetailsScreen(val qrCodeData: String, val imageUri: String) :
        AllStorageImagesEffect()

    /** Triggers analysis of an image for QR code scanning. */
    object AnalyzeImage : AllStorageImagesEffect()

    /** Requests storage permission from the user. */
    object RequestStoragePermission : AllStorageImagesEffect()

    /** Opens the app settings screen for the user to manually enable permissions. */
    object OpenAppSettings : AllStorageImagesEffect()
}
