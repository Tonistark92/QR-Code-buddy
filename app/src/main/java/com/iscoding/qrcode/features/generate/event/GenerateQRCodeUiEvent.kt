package com.iscoding.qrcode.features.generate.event

import android.graphics.Bitmap

sealed class GenerateQRCodeUiEvent {
    data class RequestShare(val bitmap: Bitmap) : GenerateQRCodeUiEvent()
    data class ShowToast(val message: String) : GenerateQRCodeUiEvent()
}



