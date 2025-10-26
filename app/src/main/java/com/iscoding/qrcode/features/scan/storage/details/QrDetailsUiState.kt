package com.iscoding.qrcode.features.scan.storage.details

/**
 * Represents the UI state of the QR Details screen.
 * This state is observed by the UI and updated by the ViewModel.
 *
 * @property qrData The actual data contained in the scanned QR code.
 * @property imageUrl The URI or path of the image containing the QR code.
 * @property errorMessage Any error message to display to the user.
 * @property isLoading Indicates whether the screen is currently performing a loading operation.
 * @property isValidUrl True if [qrData] is a valid URL, false otherwise.
 */
data class QrDetailsUiState(
    val qrData: String = "",
    val imageUrl: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = true,
    val isValidUrl: Boolean = false,
)
