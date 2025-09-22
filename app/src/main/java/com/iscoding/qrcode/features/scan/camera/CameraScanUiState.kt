package com.iscoding.qrcode.features.scan.camera

import androidx.compose.ui.geometry.Offset

data class CameraScanUiState(
    val hasCamPermission: Boolean = false,
    val shouldPermissionDialog: Boolean = false,
    val shouldURLDialog: Boolean = false,
    val shouldLaunchAppSettings: Boolean = false,
    val scannedData: String = "",
    val scannedUrl: String = "",
    val isGoodUrlRegex: Boolean = false,
    var tapPosition: Offset? = null,
)
