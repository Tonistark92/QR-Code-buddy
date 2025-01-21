package com.iscoding.qrcode.features.scancode.fromstorage.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun ShowQRCodeImageData(qrCodeData:String,imageUri:String) {
    val context = LocalContext.current
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

        val isUrl = isValidUrlWithRegex(qrCodeData)

        // Display a button to open the URL in a browser if valid
        if (isUrl) {
            Button(
                onClick = {
                    val context =context
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(qrCodeData))
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Open in Browser")
            }
        }
    }

    }

    fun isValidUrlWithRegex(url: String): Boolean {
        val urlPattern = Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""")
        return urlPattern.matches(url)
    }

