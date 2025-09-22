package com.iscoding.qrcode.features.scan.camera.intent

sealed class CameraScanEffect {
    object OpenUrl : CameraScanEffect()

    data class ShowToast(val message: String) : CameraScanEffect()

    object RequestCameraPermission : CameraScanEffect()

    object OpenAppSettings : CameraScanEffect()
}
