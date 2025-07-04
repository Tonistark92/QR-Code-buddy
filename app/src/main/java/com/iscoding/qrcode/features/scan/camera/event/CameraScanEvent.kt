package com.iscoding.qrcode.features.scan.camera.event

sealed class CameraScanEvent {

    data class OnCameraPermissionResult(val granted: Boolean) : CameraScanEvent()
    object OnValidateRegexForUrl : CameraScanEvent()
    object OnOpenUrlFromScannedQrCode : CameraScanEvent()
    data class OnScannedQrCode(val data: String) : CameraScanEvent()
    object OnRequestCameraPermission : CameraScanEvent()
    object OnOpenAppSettings : CameraScanEvent()
    object OnDismissPermissionDialog : CameraScanEvent()
    object OnDismissUrlDialog : CameraScanEvent()


}