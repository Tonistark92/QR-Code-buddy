package com.iscoding.qrcode.domain.repos

import androidx.camera.core.ImageAnalysis

interface QrCodeScanner {
    fun getAnalyzer(onResult: (String) -> Unit): ImageAnalysis.Analyzer
}
