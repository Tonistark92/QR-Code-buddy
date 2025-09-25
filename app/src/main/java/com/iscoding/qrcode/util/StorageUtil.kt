package com.iscoding.qrcode.util

import android.os.Build

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else {
        null
    }
}

// Add this new one for Android 13+
inline fun <T> sdk33AndUp(onSdk33: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onSdk33()
    } else {
        null
    }
}
