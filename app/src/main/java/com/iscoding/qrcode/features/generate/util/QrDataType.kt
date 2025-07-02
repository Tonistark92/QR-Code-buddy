package com.iscoding.qrcode.features.generate.util

import androidx.annotation.StringRes
import com.iscoding.qrcode.R


enum class QrDataType(@StringRes val labelResId: Int) {
    TEXT(R.string.qr_type_text),
    URL(R.string.qr_type_url),
    MAIL(R.string.qr_type_mail),
    TEL(R.string.qr_type_tel),
    SMS(R.string.qr_type_sms),
    GEO(R.string.qr_type_geo),
    EVENT(R.string.qr_type_event),

//    override fun toString(): String = label
}