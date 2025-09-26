package com.iscoding.qrcode.features.scan.storage.allimages

import androidx.compose.ui.geometry.Offset
import com.iscoding.qrcode.domain.model.SharedStoragePhoto

/**
 * Represents the UI state for the "Scan from Storage Images" screen.
 *
 * This state holds information about:
 * - The list of images retrieved from storage
 * - Pagination and album selection
 * - Permissions and dialogs
 * - Scanned QR code data and validation
 * - Loading and error states
 *
 * @property storageImagesList The list of images loaded from the device storage.
 * @property albums The list of album names available in the device storage.
 * @property selectedAlbum The currently selected album for filtering images. `null` means all albums.
 * @property isLoading Whether the initial image loading is in progress.
 * @property isLoadingMore Whether additional images are being loaded (pagination).
 * @property hasMoreImages Whether there are more images available to load.
 * @property currentPage The current page index for paginated image loading.
 * @property pageSize The number of images to load per page.
 * @property noQrFound Whether the last scanned image did not contain a QR code.
 * @property clickedImageUri The URI of the image that the user clicked for scanning.
 * @property hasStoragePermission Whether the app has permission to access storage.
 * @property shouldPermissionDialog Whether the permission dialog should be displayed.
 * @property shouldURLDialog Whether the URL dialog should be displayed after scanning a QR code.
 * @property shouldLaunchAppSettings Whether the user should be redirected to app settings to enable permission.
 * @property scannedData The text data obtained from the last scanned QR code.
 * @property errorMessage An error message to display in the UI.
 * @property isGoodUrlRegex Whether the scanned QR code contains a valid URL.
 * @property tapPosition The position where the user tapped, useful for focus or UI overlays.
 */
data class AllStorageImagesUiState(
    val storageImagesList: List<SharedStoragePhoto> = emptyList(),
    val albums: List<String> = emptyList(),
    val selectedAlbum: String? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreImages: Boolean = true,
    val currentPage: Int = 0,
    val pageSize: Int = 50,
    val noQrFound: Boolean = false,
    val clickedImageUri: String? = null,
    val hasStoragePermission: Boolean = false,
    val shouldPermissionDialog: Boolean = false,
    val shouldURLDialog: Boolean = false,
    val shouldLaunchAppSettings: Boolean = false,
    val scannedData: String = "",
    val errorMessage: String = "",
    val isGoodUrlRegex: Boolean = false,
    var tapPosition: Offset? = null,
)
