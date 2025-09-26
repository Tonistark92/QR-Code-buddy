package com.iscoding.qrcode.features.scan.camera.intent

import androidx.compose.ui.geometry.Offset

sealed class CameraScanEvent {
    data class OnInitialPermissionCheck(val hasPermission: Boolean) : CameraScanEvent()

    data class OnTapToFocus(val offset: Offset) : CameraScanEvent()

    object OnClearTapToFocus : CameraScanEvent()

    data class OnCameraPermissionResult(
        val granted: Boolean,
        val shouldShowRationale: Boolean,
    ) : CameraScanEvent()

    data class OnPermissionStatusChanged(val hasPermission: Boolean) : CameraScanEvent()

    object OnValidateRegexForUrl : CameraScanEvent()

    object OnOpenUrlFromScannedQrCode : CameraScanEvent()

    data class OnScannedQrCode(val data: String) : CameraScanEvent()

    object OnRequestCameraPermission : CameraScanEvent()

    object OnTextLongPressed : CameraScanEvent()

    object OnOpenAppSettings : CameraScanEvent()

    object OnDismissPermissionDialog : CameraScanEvent()

    object OnDismissUrlDialog : CameraScanEvent()
}
