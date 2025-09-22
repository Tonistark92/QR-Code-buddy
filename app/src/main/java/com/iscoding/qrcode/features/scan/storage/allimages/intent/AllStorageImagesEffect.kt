package com.iscoding.qrcode.features.scan.storage.allimages.intent

sealed class AllStorageImagesEffect {
    data class ShowToast(val message: String) : AllStorageImagesEffect()

    object RequestStoragePermission : AllStorageImagesEffect()
}
