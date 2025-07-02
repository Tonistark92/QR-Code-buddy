package com.iscoding.qrcode.domain.repos

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

interface QrCodeScanner {
    fun getAnalyzer(onResult: (String) -> Unit): ImageAnalysis.Analyzer
}