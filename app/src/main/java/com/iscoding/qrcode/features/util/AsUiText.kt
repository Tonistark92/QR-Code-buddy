package com.iscoding.qrcode.features.util

import com.iscoding.qrcode.R
import com.iscoding.qrcode.features.generate.util.QrDataType

/**
 * Converts a [QrDataType] to a [UiText] instance suitable for UI display.
 *
 * This function maps each type of QR code data to a string resource that can
 * be localized. Using [UiText] allows the UI layer to handle both string
 * resources and plain strings uniformly.
 *
 * Example usage:
 * ```
 * val qrTypeText: UiText = QrDataType.URL.asUiText()
 * ```
 */
fun QrDataType.asUiText(): UiText {
    return when (this) {
        QrDataType.TEXT -> UiText.StringResource(R.string.qr_type_text)
        QrDataType.URL -> UiText.StringResource(R.string.qr_type_url)
        QrDataType.MAIL -> UiText.StringResource(R.string.qr_type_mail)
        QrDataType.TEL -> UiText.StringResource(R.string.qr_type_tel)
        QrDataType.SMS -> UiText.StringResource(R.string.qr_type_sms)
        QrDataType.GEO -> UiText.StringResource(R.string.qr_type_geo)
        QrDataType.EVENT -> UiText.StringResource(R.string.qr_type_event)
    }
}
