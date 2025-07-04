package com.iscoding.qrcode.features.scan.camera.event

sealed class CameraScanUiEvent {
    object OpenUrl : CameraScanUiEvent()
    data class ShowToast(val message: String) : CameraScanUiEvent()
    object RequestCameraPermission : CameraScanUiEvent()
    object OpenAppSettings : CameraScanUiEvent()
}
