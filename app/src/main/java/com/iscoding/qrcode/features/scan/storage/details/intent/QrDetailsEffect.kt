package com.iscoding.qrcode.features.scan.storage.details.intent

sealed class QrDetailsEffect {
    data class ShowToast(val message: String) : QrDetailsEffect()

    data class OnQrDataLongPressed(val QrData: String) : QrDetailsEffect()

    data class OnOpenUrl(val urlString: String) : QrDetailsEffect()
}
