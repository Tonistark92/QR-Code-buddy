package com.iscoding.qrcode.features.scancode.fromstorage.presentation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun ShowQRCodeImageData(qrCodeData: String, imageUri: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        val clipboardManager = LocalClipboardManager.current // ClipboardManager instance

        Text(
            text = "QR Code Data Scanned from the image selected:",
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = qrCodeData,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            clipboardManager.setText(AnnotatedString(qrCodeData))
                            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                },
            color = MaterialTheme.colorScheme.surface
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "If you long press on it will copy in your keyboard!",
            color = MaterialTheme.colorScheme.surface
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Display the image using the provided URI
     Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
         Image(
             painter = rememberImagePainter(imageUri),
             contentDescription = null,
             contentScale = ContentScale.Fit,
             modifier = Modifier.size(300.dp)
         )

         val isUrl = isValidUrlWithRegex(qrCodeData)

         // Display a button to open the URL in a browser if valid
         if (isUrl) {
             Spacer(modifier = Modifier.height(20.dp))

             Button(
                 onClick = {
                     val context = context
                     val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(qrCodeData))
                     context.startActivity(intent)
                 },

                 shape = RectangleShape,
                 colors = ButtonDefaults.buttonColors(
                     containerColor = MaterialTheme.colorScheme.primary, // Background color
                     contentColor = Color.White, // Text/Icon color
                     disabledContainerColor = Color.Gray, // Disabled background
                     disabledContentColor = Color.Black // Disabled text color
                 ),
                 modifier = Modifier.padding(16.dp)
             ) {
                 Text("Open in Browser")
             }
         }

     }

    }

}

fun isValidUrlWithRegex(url: String): Boolean {
    val urlPattern =
        Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""")
    return urlPattern.matches(url)
}

