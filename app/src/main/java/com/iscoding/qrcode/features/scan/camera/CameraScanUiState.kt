package com.iscoding.qrcode.features.scan.camera

import android.Manifest
import androidx.compose.ui.geometry.Offset

//data class CameraScanUiState(
//    val hasCamPermission: Boolean = false,
//    var shouldPermissionDialog : Boolean = false,
//    var shouldURLDialog : Boolean = false,
//    var shouldLaunchAppSettings : Boolean = false,
//    var isGoodUrlRegex : Boolean = false,
//    var scannedUrl : String = "",
//    var scannedData : String = "",
//    var tapPosition : Offset?= null,
//
//)
data class CameraScanUiState(
    val hasCamPermission: Boolean = false,
    val shouldPermissionDialog: Boolean = false,
    val shouldURLDialog: Boolean = false,
    val shouldLaunchAppSettings: Boolean = false,
    val scannedData: String = "",
    val scannedUrl: String = "",
    val isGoodUrlRegex: Boolean = false,
    val tapPosition: Offset? = null
)