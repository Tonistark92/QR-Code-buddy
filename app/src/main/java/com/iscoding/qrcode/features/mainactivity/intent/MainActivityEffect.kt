package com.iscoding.qrcode.features.mainactivity.intent

import android.net.Uri

sealed class MainActivityEffect {
    data class ShowToast(val message: String) : MainActivityEffect()

    data class NavigateToQrDetailsScreen(
        val qrCode: String,
        val imageUri: Uri,
    ) : MainActivityEffect()

    object AnalyzeImage : MainActivityEffect()
}
