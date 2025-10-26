package com.iscoding.qrcode.util

import android.os.Build

/**
 * Executes the given [onSdk29] block only if the device is running
 * **Android 10 (API 29 / Q)** or higher.
 *
 * Example:
 * ```
 * sdk29AndUp {
 *     // Safe to call APIs introduced in Android 10+
 *     val mimeGroup = intent.getStringExtra(Intent.EXTRA_MIME_TYPES)
 * }
 * ```
 *
 * @param onSdk29 The block of code to execute if the SDK version is >= 29.
 * @return The result of [onSdk29] if executed, or `null` otherwise.
 */
inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29()
    } else {
        null
    }
}

/**
 * Executes the given [onSdk33] block only if the device is running
 * **Android 13 (API 33 / Tiramisu)** or higher.
 *
 * Example:
 * ```
 * sdk33AndUp {
 *     // Safe to call APIs introduced in Android 13+
 *     val permission = context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
 * }
 * ```
 *
 * @param onSdk33 The block of code to execute if the SDK version is >= 33.
 * @return The result of [onSdk33] if executed, or `null` otherwise.
 */
inline fun <T> sdk33AndUp(onSdk33: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onSdk33()
    } else {
        null
    }
}
