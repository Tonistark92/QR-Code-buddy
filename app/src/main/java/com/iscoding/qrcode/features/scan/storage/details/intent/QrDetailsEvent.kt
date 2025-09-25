package com.iscoding.qrcode.features.scan.storage.details.intent

sealed class QrDetailsEvent {

    data class OnInitialDetailsScreen(
        val qrCodeData: String,
        val imageUri: String,
    ) : QrDetailsEvent()

    object OnOpenUrl : QrDetailsEvent()

    object OnQrDataLongPressed : QrDetailsEvent()
}
