package com.iscoding.qrcode.features.generatecode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.wifi.WifiManager
import android.util.Log
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
import com.iscoding.qrcode.features.generatecode.widgets.SmsInput
import com.iscoding.qrcode.features.generatecode.widgets.TelInput
import com.iscoding.qrcode.features.generatecode.widgets.TextInput
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



    val viewmodel = getViewModel<GenerateQRCodeViewModel>()
//    val viewmodel = koinViewModel<GenerateQRCodeViewModel>()
    val state = viewmodel.state.collectAsState()


    val coroutineScope = rememberCoroutineScope()
    val shareImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    val context = LocalContext.current
    val dataTypesList = listOf("URL", "Mail", "Tel", "SMS", "Geo", "WIFI", "Event", "Text")
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
        }


        if (choosenData.value == "Mail") {
            TextField(
                value = state.value.mail,
                onValueChange = { newText ->
                    state.value.mail= newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        val emailPattern =
                            Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}""")
                        state.value.shouldShowErrorMail = newText.matches(emailPattern)
                    }

                },
                label = { Text("Type The Mail") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (state.value.shouldShowErrorMail) {
                Text(text = state.value.errorMessageMail)
                Spacer(modifier = Modifier.height(6.dp))
            }


        }
        if (choosenData.value == "URL") {
            TextField(
                value = state.value.url,
                onValueChange = { newText ->
                    state.value.url = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        val urlPattern =
                            Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""")
                        state.value.shouldShowErrorUrl = newText.matches(urlPattern)
                    }

                },
                label = { Text("Type The URL") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (state.value.shouldShowErrorUrl) {
                Text(text = state.value.errorMessageUrl)
                Spacer(modifier = Modifier.height(6.dp))
            }


        }



        if (choosenData.value == "Geo") {
            TextField(
                value = state.value.geoLatitude,
                onValueChange = { newText ->
                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        val regexPattern = Regex("^-?([1-8]?[1-9]|[1-9]0)\\.{0,1}\\d{0,6}$")
                        state.value.geoLatitude = newText

                        state.value.shouldShowErrorGeoLatitude =
                            newText.matches(regexPattern)
                    }

                },
                label = { Text("Type The Latitude") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (state.value.shouldShowErrorGeoLatitude) {
                Text(text = state.value.errorMessageGeoLatitude)
                Spacer(modifier = Modifier.height(6.dp))
            }

            TextField(
                value = state.value.geoLongitude,
                onValueChange = { newText ->
                    state.value.geoLongitude = newText
                    val regexPattern = Regex("^-?((1?[0-7]?|[0-9]?)[0-9]|180)\\.{0,1}\\d{0,6}$")
                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        state.value.geoLongitude = newText

                        state.value.shouldShowErrorGeoLongitude =
                            newText.matches(regexPattern)
                    }


                },
                label = { Text("Type The Longitude") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (state.value.shouldShowErrorGeoLongitude) {
                Text(text = state.value.errorMessageGeoLongitude)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        if (choosenData.value == "Event") {
            //added edit texts for all its data
            TextField(
                value = state.value.eventSubject,
                onValueChange = { newText ->
                    state.value.eventSubject = newText

                    coroutineScope.launch(Dispatchers.IO) {
                        delay(3000)
                        state.value.shouldShowErrorEventSubject =
                            newText.isNotEmpty() && newText.length < 4
                    }


                },
                label = { Text("Type The Subject For The Event") },
                placeholder = { Text(text = "ex : Meeting") },
                isError = state.value.shouldShowErrorEventSubject
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (state.value.shouldShowErrorEventSubject) {
                Text(text = state.value.errorMessageEventSubject)
            }
            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = state.value.eventDTStart,
                onValueChange = { newText ->
                    state.value.eventDTStart = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        state.value.shouldShowErrorEventDTStart =
                            newText.matches(Regex("^\\d{8}T\\d{6}$"))
                    }


                },
                label = { Text("Type The Event Start Time And Date") },
                placeholder = { Text(text = "ex time in 24  : 20240622T190000") },
                isError = state.value.shouldShowErrorEventDTStart
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (state.value.shouldShowErrorEventDTStart) {
                Text(text = state.value.errorMessageEventDTStart)
                Spacer(modifier = Modifier.height(6.dp))

            }
            TextField(
                value = state.value.eventDTEnd,
                onValueChange = { newText ->
                    state.value.eventDTEnd = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        state.value.shouldShowErrorEventDTEnd =
                            newText.matches(Regex("^\\d{8}T\\d{6}$"))
                    }


                },
                label = { Text("Type The Event End Time And Date") },
                placeholder = { Text(text = "ex time in 24  : 20240622T190000") },
                isError = state.value.shouldShowErrorEventDTStart
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (state.value.shouldShowErrorEventDTEnd) {
                Text(text = state.value.errorMessageEventDTEnd)
                Spacer(modifier = Modifier.height(6.dp))
            }
            TextField(
                value = state.value.eventLocation,
                onValueChange = { newText ->
                    state.value.eventLocation = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        state.value.shouldShowErrorEventDTEnd =
                            newText.isNotEmpty() && newText.length > 4
                    }


                },
                label = { Text("Entering Location for The Event") },
                placeholder = { Text(text = "ex : Office") },
                isError = state.value.shouldShowErrorEventDTStart
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (state.value.shouldShowErrorEventLocation) {
                Text(text = state.value.errorMessageEventLocation)
                Spacer(modifier = Modifier.height(6.dp))
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
            },
            enabled = state.value.qrBitmap != null // Disable the button if qrBitmapState is null
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

fun formatData(type: String, data: Map<String, String>): Result<String> {
    return when (type) {
        "URL" -> {
            val url =
                data["text"] ?: return Result.failure(IllegalArgumentException("URL is missing."))
            if (url.startsWith("http://") || url.startsWith("https://")) {
                Result.success(url)
            } else {
                Result.failure(IllegalArgumentException("Invalid URL format. Must start with http:// or https://"))
            }
        }

        "Mail" -> {
            val email =
                data["text"] ?: return Result.failure(IllegalArgumentException("Email is missing."))
            if (email.startsWith("mailto:")) {
                Result.success(email)
            } else if (email.contains("@")) {
                Result.success("mailto:$email")
            } else {
                Result.failure(IllegalArgumentException("Invalid email address."))
            }
        }

        "Tel" -> {
            val phone = data["text"]
                ?: return Result.failure(IllegalArgumentException("Phone number is missing."))
            if (phone.startsWith("tel:")) {
                Result.success(phone)
            } else {
                Result.success("tel:$phone")
            }
        }

        "SMS" -> {
            val sms = data["text"]
                ?: return Result.failure(IllegalArgumentException("SMS number is missing."))
            if (sms.startsWith("sms:")) {
                Result.success(sms)
            } else {
                Result.success("sms:$sms")
            }
        }

        "Geo" -> {
            val geo = data["text"]
                ?: return Result.failure(IllegalArgumentException("Geo coordinates are missing."))
            if (geo.startsWith("geo:")) {
                Result.success(geo)
            } else {
                Result.success("geo:$geo")
            }
        }

        "Event" -> {
            val summary = data["summary"]
                ?: return Result.failure(IllegalArgumentException("Event summary is missing."))
            val dtstart = data["dtstart"]
                ?: return Result.failure(IllegalArgumentException("Event start date is missing."))
            val dtend = data["dtend"]
                ?: return Result.failure(IllegalArgumentException("Event end date is missing."))
            val location = data["location"]
                ?: return Result.failure(IllegalArgumentException("Event location is missing."))

            val event = """
            BEGIN:VEVENT
            SUMMARY:$summary
            DTSTART:$dtstart
            DTEND:$dtend
            LOCATION:$location
            END:VEVENT
            """.trimIndent()
            Result.success(event)
        }

        "Text" -> {
            val text =
                data["text"] ?: return Result.failure(IllegalArgumentException("Text is missing."))
            Result.success(text)
        }

        else -> Result.failure(IllegalArgumentException("Unknown data type"))
    }
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