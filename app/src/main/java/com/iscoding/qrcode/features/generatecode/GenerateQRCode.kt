package com.iscoding.qrcode.features.generatecode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.iscoding.qrcode.features.generatecode.widgets.EventInput
import com.iscoding.qrcode.features.generatecode.widgets.GeoInput
import com.iscoding.qrcode.features.generatecode.widgets.MailInput
import com.iscoding.qrcode.features.generatecode.widgets.SmsInput
import com.iscoding.qrcode.features.generatecode.widgets.TelInput
import com.iscoding.qrcode.features.generatecode.widgets.TextInput
import com.iscoding.qrcode.features.generatecode.widgets.URLInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Hashtable

// ToDo: Formate the data for generating the qr
// ToDo: create Button for each case
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQRCode() {


//    val viewmodel = getViewModel<GenerateQRCodeViewModel>()
    val viewmodel = koinViewModel<GenerateQRCodeViewModel>()
    val state = viewmodel.state.collectAsState()


    val coroutineScope = rememberCoroutineScope()
    val shareImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    val context = LocalContext.current
    val dataTypesList = listOf("URL", "Mail", "Tel", "SMS", "Geo", "Event", "Text")
    var choosenData = remember {
        mutableStateOf("Text")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        state.value.qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code"
            )
        } ?: Image(
            painter = painterResource(id = android.R.drawable.ic_menu_report_image),
            contentDescription = "Placeholder"
        )
        Spacer(modifier = Modifier.height(6.dp))

        var expandedDatatype = remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedDatatype.value,
            onExpandedChange = {
                expandedDatatype.value = !expandedDatatype.value
            }
        ) {

            TextField(
                value = state.value.pickedType,
                onValueChange = { state.value.pickedType = it },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDatatype.value) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedDatatype.value,
                onDismissRequest = {
                    expandedDatatype.value = false
                }
            ) {
                dataTypesList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            choosenData.value = item
                            state.value.pickedType = item
                            expandedDatatype.value = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        fun updateState(newState: GenerateQRCodeState) {
            coroutineScope.launch {
                viewmodel.updateState(newState)
            }
        }

        when (choosenData.value) {
            "Text" -> TextInput(state.value, ::updateState)
            "Tel" -> TelInput(state.value, ::updateState)
            "SMS" -> SmsInput(state.value, ::updateState)
            "Mail" -> MailInput(state.value, coroutineScope)
            "URL" -> URLInput(state.value, coroutineScope)
            "Geo" -> GeoInput(state.value, coroutineScope)
            "Event" -> EventInput(state.value, coroutineScope)
        }
        // add button for generating

    }
    // for share
    Button(
        onClick = {
            state.value.qrBitmap?.let {
//                    Log.d("QRCode", it.byteCount.toString())
                val uri = getImageUri(context, it)
                Log.d("QRCode", uri.toString())
                uri?.let { imageUri ->
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        type = "image/png"
                    }
                    shareImageLauncher.launch(
                        Intent.createChooser(
                            shareIntent,
                            "Share QR Code"
                        )
                    )
                }
            }
        },
        enabled = state.value.qrBitmap != null // Disable the button if qrBitmapState is null
    ) {
        Text("Generate QR Code")
    }
    // for generate
    Button(
        onClick = {
            state.value.qrBitmap.let {
//                    Log.d("QRCode", it?.byteCount.toString())

//                val isRightData =
//                    viewmodel.validateInput(state = state.value, chosenData = choosenData.value)
                val isRightData = formatData(choosenData.value,state.value,viewmodel)
                if (isRightData) {
                    val uri = it?.let { it1 -> getImageUri(context, it1) }
                    Log.d("QRCode", uri.toString())
                    uri?.let { imageUri ->
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, imageUri)
                            type = "image/png"
                        }
                        shareImageLauncher.launch(
                            Intent.createChooser(
                                shareIntent,
                                "Share QR Code"
                            )
                        )
                    }

                }
                else{
                    Toast.makeText(context,"Please fit the mentioned examples",Toast.LENGTH_SHORT).show()
                }
            }
        },
//            enabled = state.value.qrBitmap != null // Disable the button if qrBitmapState is null
    ) {
        Text("Share QR Code")
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

fun formatData(type: String, state: GenerateQRCodeState, viewModel: GenerateQRCodeViewModel) : Boolean{
    var isFormattedAndReady = false
     when (type) {
         "URL" -> {
             val isUrlReady = viewModel.validateInput("URL", state)
             if (isUrlReady) {
//                 viewModel.updateState(state.copy(url = state.url))
                 viewModel.updateState(state.copy(formattedUrl = state.url))
                 isFormattedAndReady= true
             }
         }

         "Mail" -> {
             val isEmailReady = viewModel.validateInput("Mail", state)

             if (isEmailReady) {
                 if (state.mail.startsWith("mailto:")) {
                     viewModel.updateState(state.copy(mail = state.mail))
                     isFormattedAndReady= true

                 } else {
                     viewModel.updateState(state.copy(formattedMail = "mailto:${state.mail}"))
                     isFormattedAndReady= true


                 }
             }
         }

         "Tel" -> {
             val isPhoneReady = viewModel.validateInput("Tel", state)
             if (isPhoneReady) {
                 if (state.tel.startsWith("tel:")) {
                     viewModel.updateState(state.copy(formattedTel = state.tel))
                     isFormattedAndReady= true


                 } else {
                     viewModel.updateState(state.copy(formattedTel = "tel:${state.tel}"))
                     isFormattedAndReady= true

                 }
             }
         }

         "SMS" -> {
             val isSmsReady = viewModel.validateInput("SMS", state)
             if (isSmsReady) {
                 if (state.formattedSMS.startsWith("sms:")) {
                     viewModel.updateState(state.copy(smsData = state.smsData))
                     viewModel.updateState(state.copy(smsNumber = state.smsNumber))
                     val formatedSMS = """
                         Tel: ${state.smsNumber}
                         data: ${state.smsData}
                     """.trimMargin()

                     viewModel.updateState(state.copy(formattedSMS = formatedSMS))
                     isFormattedAndReady= true


                 } else {
                     viewModel.updateState(state.copy(smsData = "sms:${state.smsData}"))
                     viewModel.updateState(state.copy(smsNumber = state.smsNumber))
                     val formatedSMS = """
                         sms:
                         Tel: ${state.smsNumber}
                         data: ${state.smsData}
                     """.trimMargin()

                     viewModel.updateState(state.copy(formattedSMS = formatedSMS))
                     isFormattedAndReady= true

                 }
             }
         }

        "Geo" -> {
            val isGeoReady = viewModel.validateInput("Geo", state)
            if(isGeoReady) {
                if (state.formattedGeo.startsWith("geo:")) {
                    val formatedGeo = """
                         lat: ${state.geoLatitude}
                         long: ${state.geoLongitude}
                     """.trimMargin()

                    viewModel.updateState(state.copy(formattedGeo = formatedGeo))
                    isFormattedAndReady= true

                } else {
                    val formatedGeo = """
                         geo:
                         lat: ${state.geoLatitude}
                         long: ${state.geoLongitude}
                     """.trimMargin()

                    viewModel.updateState(state.copy(formattedGeo = formatedGeo))
                    isFormattedAndReady= true

                }
            }
        }

        "Event" -> {
            val isSummaryReady =  viewModel.validateInput("Event", state)

            val formattedEvent = """
            BEGIN:EVENT
            SUMMARY:${state.eventSubject}
            DTSTART:${state.eventDTStart}
            DTEND:${state.eventDTEnd}
            LOCATION:${state.eventLocation}
            END:EVENT
            """.trimIndent()
            viewModel.updateState(state.copy(formattedEvent = formattedEvent))
            isFormattedAndReady= true

        }

        "Text" -> {
            val isTextReady = viewModel.validateInput("Text", state)
            if (isTextReady){

                viewModel.updateState(state.copy(formattedText = state.plainText))
                isFormattedAndReady= true

            }

        }

        else -> {
            isFormattedAndReady= false

        }

    }
    return isFormattedAndReady
}

fun generateQRCode(data: String, width: Int = 512, height: Int = 512): Bitmap? {
    return try {

        val writer = QRCodeWriter()
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
                )
            }
        }
        bmp
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

}

//// Assume the user entered this date-time string
//val userEnteredDateTimeString = "20240622T190000"
//
//// Parse the user-entered date-time string
//val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
//val localDateTime = LocalDateTime.parse(userEnteredDateTimeString, formatter)
//
//// Get the user's local time zone
//val userZoneId = ZoneId.systemDefault()
//
//// Convert LocalDateTime to ZonedDateTime with user's local time zone
//val zonedDateTime = localDateTime.atZone(userZoneId)
//
//// Format the ZonedDateTime to a string including the time zone info
//val formattedDateTime = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z"))
//
//// Print the formatted date-time string
//println("User's local date-time: $formattedDateTime")
//
//// Additionally, convert the ZonedDateTime to UTC and format it
//val utcZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"))
//val formattedUtcDateTime = utcZonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
//
//// Print the formatted UTC date-time string
//println("UTC date-time: $formattedUtcDateTime")

//        TextField(
//            value = textState.value,
//            onValueChange = { newText ->
//                textState.value = newText
//                coroutineScope.launch(Dispatchers.Default) {
//                    delay(2000) // Wait for 2 seconds
//                    val writer = QRCodeWriter()
//                    val hints = Hashtable<EncodeHintType, Any>()
//                    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
//                    val bitMatrix = writer.encode(newText,BarcodeFormat.QR_CODE,512,512,hints)
//                    val width = bitMatrix.width
//                    val hight = bitMatrix.height
//                    val bmp = Bitmap.createBitmap(width,hight,Bitmap.Config.RGB_565)
//                    for (x in 0 until width){
//                        for (y in 0 until  hight){
//                            bmp.setPixel(x,y,if(bitMatrix[x,y]) Color.Black.toArgb() else Color.White.toArgb())
//                        }
//                    }
//                    qrBitmapState.value = bmp
//                }
//
//            },
//            label = { Text("Enter text") }
//        )
//coroutineScope.launch(Dispatchers.Default) {
//    if (choosenData.value == "Geo" && otherTextState.value.isNotEmpty() ||  choosenData.value == "Event" && otherTextState.value.isNotEmpty() ){
//        delay(2000) // Wait for 2 seconds
//        val writer = QRCodeWriter()
//        val hints = Hashtable<EncodeHintType, Any>()
//        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
//        val bitMatrix = writer.encode(newText,BarcodeFormat.QR_CODE,512,512,hints)
//        val width = bitMatrix.width
//        val hight = bitMatrix.height
//        val bmp = Bitmap.createBitmap(width,hight,Bitmap.Config.RGB_565)
//        for (x in 0 until width){
//            for (y in 0 until  hight){
//                bmp.setPixel(x,y,if(bitMatrix[x,y]) Color.Black.toArgb() else Color.White.toArgb())
//            }
//        }
//        qrBitmapState.value = bmp
//    }
//
//}