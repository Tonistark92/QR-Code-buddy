package com.iscoding.qrcode.generatecode

import android.graphics.Bitmap
import android.provider.CalendarContract.Colors
import android.widget.EditText
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GenerateQRCode() {
    val textState = remember { mutableStateOf("") }
    val qrBitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        qrBitmapState.value?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code"
            )
        } ?: Image(
            painter = painterResource(id = android.R.drawable.ic_menu_report_image), // replace with your drawable resource
            contentDescription = "Placeholder"
        )

        TextField(
            value = textState.value,
            onValueChange = { newText ->
                textState.value = newText
                coroutineScope.launch(Dispatchers.Default) {
                    delay(2000) // Wait for 2 seconds
                    val writer = QRCodeWriter()
                    val bitMatrix = writer.encode(newText,BarcodeFormat.QR_CODE,512,512)
                    val width = bitMatrix.width
                    val hight = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width,hight,Bitmap.Config.RGB_565)
                    for (x in 0 until width){
                        for (y in 0 until  hight){
                            bmp.setPixel(x,y,if(bitMatrix[x,y]) Color.Black.toArgb() else Color.White.toArgb())
                        }
                    }
                    qrBitmapState.value = bmp
                }

            },
            label = { Text("Enter text") }
        )

    }


}