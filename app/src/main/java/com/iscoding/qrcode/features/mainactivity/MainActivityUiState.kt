package com.iscoding.qrcode.features.mainactivity

import android.net.Uri

data class MainActivityUiState(
    val isLoading: Boolean = false,
    val noQrFound: Boolean = false,
    val qrCode: String = "",
    val imageUri: Uri? = null,
)
