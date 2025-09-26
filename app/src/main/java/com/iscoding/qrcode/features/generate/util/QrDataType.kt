package com.iscoding.qrcode.features.generate.util

import androidx.annotation.StringRes
import com.iscoding.qrcode.R

/**
 * Represents the different types of QR code data that can be generated.
 *
 * Each type is associated with a string resource ID, which should be used
 * to display the human-readable label in the UI.
 *
 * @property labelResId The string resource ID that represents the label
 * for this QR data type.
 */
enum class QrDataType(
    @StringRes val labelResId: Int,
) {
    /** Plain text QR code. */
    TEXT(R.string.qr_type_text),

    /** URL (web link) QR code. */
    URL(R.string.qr_type_url),

    /** Email address QR code. */
    MAIL(R.string.qr_type_mail),

    /** Telephone number QR code. */
    TEL(R.string.qr_type_tel),

    /** SMS message QR code. */
    SMS(R.string.qr_type_sms),

    /** Geographic coordinates QR code. */
    GEO(R.string.qr_type_geo),

    /** Calendar event QR code. */
    EVENT(R.string.qr_type_event),
}
