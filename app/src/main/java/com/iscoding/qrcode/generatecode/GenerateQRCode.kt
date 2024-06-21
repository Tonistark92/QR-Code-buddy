package com.iscoding.qrcode.generatecode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.CalendarContract.Colors
import android.util.Log
import android.widget.EditText
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Hashtable

@Composable
fun GenerateQRCode() {
    val textState = remember { mutableStateOf("") }
    val qrBitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val shareImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    val context = LocalContext.current
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
                    val hints = Hashtable<EncodeHintType, Any>()
                    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
                    val bitMatrix = writer.encode(newText,BarcodeFormat.QR_CODE,512,512,hints)
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
        Button(
            onClick = {
                qrBitmapState.value?.let {
                    Log.d("QRCode", it.byteCount.toString())
                    val uri = getImageUri(context, it)
                    Log.d("QRCode", uri.toString())
                    uri?.let { imageUri ->
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, imageUri)
                            type = "image/png"
                        }
                        shareImageLauncher.launch(Intent.createChooser(shareIntent, "Share QR Code"))
                    }
                }
            },
            enabled = qrBitmapState.value != null // Disable the button if qrBitmapState is null
        ) {
            Text("Share QR Code")
        }
        }



}
fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
    val imagesFolder = File(context.cacheDir, "images")
    var uri: Uri? = null
    try {
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "shared_image.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        stream.flush()
        stream.close()
        uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return uri
}