package com.iscoding.qrcode.domain.repos

import android.net.Uri
import java.io.InputStream

interface QrCodeStorageAnalyzer {
    fun analyze(
        uri: Uri,
        inputStream: InputStream,
        onNoQRCodeFound: () -> Unit,
        onQrCodeScanned: (String) -> Unit,
    )
}
