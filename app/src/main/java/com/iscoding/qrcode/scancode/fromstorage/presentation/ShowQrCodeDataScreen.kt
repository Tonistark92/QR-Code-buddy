package com.iscoding.qrcode.scancode.fromstorage.presentation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun ShowQRCodeImageData(qrCodeData:String,imageUri:String) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = "QR Code Data: $qrCodeData",
            modifier = Modifier.padding(16.dp)
        )

        // Display the image using the provided URI
        Image(
            painter = rememberImagePainter(imageUri),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(300.dp)
        )

    }
}