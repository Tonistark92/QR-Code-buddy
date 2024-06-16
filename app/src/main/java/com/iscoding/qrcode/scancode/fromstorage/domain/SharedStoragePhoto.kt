package com.iscoding.qrcode.scancode.fromstorage.domain

import android.graphics.Bitmap
import android.net.Uri

data class SharedStoragePhoto(
    val id: Long,
    val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri,
//    val album:String,
)