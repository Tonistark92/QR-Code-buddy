package com.iscoding.qrcode.features.mainactivity

import android.content.Intent
import android.net.Uri

/**
 * Represents the UI state of the MainActivity screen in the QR code app.
 *
 * This state is used to render the UI in a Compose-based MainActivity and is updated
 * by the ViewModel in response to user actions or background processing, such as
 * scanning a QR code or loading an image from storage.
 */
data class MainActivityUiState(
    /**
     * Indicates whether the screen should display a loading indicator.
     * Typically true while processing QR code generation or scanning.
     */
    val isLoading: Boolean = false,

    /**
     * Indicates whether no QR code was found during a scan or image analysis.
     * Can be used to display an error message or empty state in the UI.
     */
    val noQrFound: Boolean = false,

    /**
     * Holds the decoded QR code string after a successful scan or image analysis.
     * If empty, no QR code has been detected yet.
     */
    val qrCode: String = "",
    val intent: Intent? = null,

    /**
     * Holds the URI of an image selected by the user for QR code scanning.
     * Null if no image is selected.
     */
    val imageUri: Uri? = null,
)
