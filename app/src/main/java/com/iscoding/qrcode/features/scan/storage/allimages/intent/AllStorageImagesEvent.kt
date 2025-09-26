package com.iscoding.qrcode.features.scan.storage.allimages.intent

import android.net.Uri
import java.io.InputStream

sealed class AllStorageImagesEvent {
    data class OnInitialPermissionCheck(val hasPermission: Boolean) : AllStorageImagesEvent()

    data class OnStoragePermissionResult(
        val granted: Boolean,
        val shouldShowRationale: Boolean,
    ) : AllStorageImagesEvent()

    data class OnPermissionStatusChanged(val hasPermission: Boolean) : AllStorageImagesEvent()

    data class OnStorageImageClicked(val uri: Uri, val inputStream: InputStream) : AllStorageImagesEvent()

    object OnOpenAppSettings : AllStorageImagesEvent()

    object OnRequestStoragePermission : AllStorageImagesEvent()

    object OnDismissPermissionDialog : AllStorageImagesEvent()

    object LoadInitialData : AllStorageImagesEvent()

    data class SelectAlbum(val albumName: String?) : AllStorageImagesEvent()

    object RefreshImages : AllStorageImagesEvent()

    object LoadMoreImages : AllStorageImagesEvent()
//    data class LoadImagesForAlbum(val context: Context, val albumName: String) : AllStorageImagesEvent()

    data class OnAnalyzeImage(val uri: Uri, val inputStream: InputStream) : AllStorageImagesEvent()
}
