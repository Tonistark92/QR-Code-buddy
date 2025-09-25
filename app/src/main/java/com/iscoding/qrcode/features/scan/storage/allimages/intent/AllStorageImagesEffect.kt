package com.iscoding.qrcode.features.scan.storage.allimages.intent

sealed class AllStorageImagesEffect {

    data class ShowToast(val message: String) : AllStorageImagesEffect()

    data class NavigateToQrDetailsScreen(val qrCodeData: String, val imageUri: String) : AllStorageImagesEffect()
    object AnalyzeImage : AllStorageImagesEffect()

    object RequestStoragePermission : AllStorageImagesEffect()

    object OpenAppSettings : AllStorageImagesEffect()
}
