package com.iscoding.qrcode.domain.repos

import android.graphics.Bitmap

interface QrCodeGenerator {
    fun generate(
        data: String,
        width: Int,
        height: Int,
    ): Bitmap
}
