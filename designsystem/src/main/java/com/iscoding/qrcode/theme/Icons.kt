package com.iscoding.qrcode.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.staticCompositionLocalOf

object QrBuddyIcons {
    val Add = Icons.Rounded.Add
    val Delete = Icons.Rounded.Delete
}

internal val LocalQrBuddyIcons = staticCompositionLocalOf { QrBuddyIcons }