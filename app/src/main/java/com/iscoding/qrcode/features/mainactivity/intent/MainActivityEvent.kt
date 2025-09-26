package com.iscoding.qrcode.features.mainactivity.intent

import android.net.Uri
import java.io.InputStream

sealed class MainActivityEvent {
    data class OnAnalyzeImage(val uri: Uri, val inputStream: InputStream) : MainActivityEvent()

    object OnNewIntentReceived : MainActivityEvent()
}
