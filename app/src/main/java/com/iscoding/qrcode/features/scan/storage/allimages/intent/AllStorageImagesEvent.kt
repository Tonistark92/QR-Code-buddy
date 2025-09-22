package com.iscoding.qrcode.features.scan.storage.allimages.intent

sealed class AllStorageImagesEvent {
    data class OnInitialPermissionCheck(val hasPermission: Boolean) : AllStorageImagesEvent()

    data class OnStoragePermissionResult(
        val granted: Boolean,
        val shouldShowRationale: Boolean,
    ) : AllStorageImagesEvent()

    data class OnPermissionStatusChanged(val hasPermission: Boolean) : AllStorageImagesEvent()

    data class OnScannedQrCode(val data: String) : AllStorageImagesEvent()

    object OnRequestStoragePermission : AllStorageImagesEvent()

    object OnDismissPermissionDialog : AllStorageImagesEvent()
}
