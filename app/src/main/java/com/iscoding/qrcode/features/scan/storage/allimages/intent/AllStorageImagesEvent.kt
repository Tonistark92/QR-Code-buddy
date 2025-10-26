package com.iscoding.qrcode.features.scan.storage.allimages.intent

import android.net.Uri
import java.io.InputStream

/**
 * Represents all possible user or system events in the "All Storage Images" screen.
 *
 * Events are processed by the ViewModel to update UI state or trigger side-effects.
 */
sealed class AllStorageImagesEvent {

    /**
     * Triggered when the screen first checks whether storage permission is granted.
     *
     * @param hasPermission `true` if permission is already granted, `false` otherwise.
     */
    data class OnInitialPermissionCheck(val hasPermission: Boolean) : AllStorageImagesEvent()

    /**
     * Triggered when the user responds to a storage permission request.
     *
     * @param granted `true` if permission was granted.
     * @param shouldShowRationale `true` if the system recommends showing rationale for permission.
     */
    data class OnStoragePermissionResult(
        val granted: Boolean,
        val shouldShowRationale: Boolean,
    ) : AllStorageImagesEvent()

    /**
     * Triggered when the storage permission status changes.
     *
     * @param hasPermission `true` if permission is currently granted.
     */
    data class OnPermissionStatusChanged(val hasPermission: Boolean) : AllStorageImagesEvent()

    /**
     * Triggered when the user clicks on an image in the storage gallery.
     *
     * @param uri The URI of the clicked image.
     * @param inputStream InputStream for accessing the image content.
     */
    data class OnStorageImageClicked(val uri: Uri, val inputStream: InputStream) : AllStorageImagesEvent()

    /** Triggered when the user chooses to open the app settings screen. */
    object OnOpenAppSettings : AllStorageImagesEvent()

    /** Triggered when the user requests storage permission manually. */
    object OnRequestStoragePermission : AllStorageImagesEvent()

    /** Triggered when the permission dialog is dismissed. */
    object OnDismissPermissionDialog : AllStorageImagesEvent()

    /** Triggered to load the initial set of images when the screen opens. */
    object LoadInitialData : AllStorageImagesEvent()

    /**
     * Triggered when the user selects an album to filter images.
     *
     * @param albumName Name of the selected album. `null` indicates no filtering.
     */
    data class SelectAlbum(val albumName: String?) : AllStorageImagesEvent()

    /** Triggered to refresh the list of images, e.g., pull-to-refresh. */
    object RefreshImages : AllStorageImagesEvent()

    /** Triggered to load more images for pagination. */
    object LoadMoreImages : AllStorageImagesEvent()

    /**
     * Triggered to analyze a specific image for QR code scanning.
     *
     * @param uri The URI of the image to analyze.
     * @param inputStream InputStream of the image content.
     */
    data class OnAnalyzeImage(val uri: Uri, val inputStream: InputStream) : AllStorageImagesEvent()
}
