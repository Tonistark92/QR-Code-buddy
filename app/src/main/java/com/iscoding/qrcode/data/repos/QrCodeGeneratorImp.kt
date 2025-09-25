package com.iscoding.qrcode.data.repos

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.iscoding.qrcode.domain.repos.QrCodeGenerator
import java.util.Hashtable

class QrCodeGeneratorImp : QrCodeGenerator {
    override fun generate(
        data: String,
        width: Int,
        height: Int,
    ): Bitmap {
        val writer = QRCodeWriter()
        val hints =
            Hashtable<EncodeHintType, Any>().apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints)
        val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp[x, y] =
                    if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
            }
        }

        return bmp
    }
}
