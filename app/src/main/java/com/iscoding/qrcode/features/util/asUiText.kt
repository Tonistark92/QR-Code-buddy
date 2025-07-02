package com.iscoding.qrcode.features.util

import com.iscoding.qrcode.R
import com.iscoding.qrcode.features.generate.util.QrDataType

fun QrDataType.asUiText(): UiText {


    return when (this) {
        QrDataType.TEXT ->  UiText.StringResource(R.string.qr_type_text)
        QrDataType.URL -> UiText.StringResource(R.string.qr_type_url)
        QrDataType.MAIL -> UiText.StringResource(R.string.qr_type_mail)
        QrDataType.TEL -> UiText.StringResource(R.string.qr_type_tel)
        QrDataType.SMS -> UiText.StringResource(R.string.qr_type_sms)
        QrDataType.GEO -> UiText.StringResource(R.string.qr_type_geo)
        QrDataType.EVENT -> UiText.StringResource(R.string.qr_type_event)
    }
}