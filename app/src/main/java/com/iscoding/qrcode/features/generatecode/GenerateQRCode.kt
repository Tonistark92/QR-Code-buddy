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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Hashtable
// ToDo: Formate the data for generating the qr
// ToDo: create Button for each case
// ToDo: Move the states to teh viewmodel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQRCode() {
    val pickedTypeState = remember { mutableStateOf("") }

    val wifiSSIDState = remember { mutableStateOf("") }
    val errorMessageWifiSSIDState = remember { mutableStateOf("Enter the Network Name ") }
    val shouldShowErrorWIFISSIDState = remember { mutableStateOf(false) }
    val encryptionWifiTypesList = listOf("WEP", "WPA", "WPA2", "nopass")
    var choosenEncryptionState = remember {
        mutableStateOf("")
    }
    var choosenWifiEncryptionType = remember {
        mutableStateOf("nopass")
    }
    val wifiPasswordState = remember { mutableStateOf("") }
    val shouldShowErrorWifiPasswordState = remember { mutableStateOf(false) }
    val errorMessageWifiPasswordState = remember { mutableStateOf("Enter a valid Wifi Password (More than 8 digits)") }
    val isWifiHiddenState = remember { mutableStateOf(false) }


    val smsNumberState = remember { mutableStateOf("") }
    val smsDataState = remember { mutableStateOf("") }
    val errorMessageSmsNumberState = remember { mutableStateOf("Enter a valid SMS Number") }
    val errorMessageSmsDataState =
        remember { mutableStateOf("Enter a valid SMS Data (more 4 digits) ") }
    val shouldShowErrorSmsNumberState = remember { mutableStateOf(false) }
    val shouldShowErrorSmsDataState = remember { mutableStateOf(false) }

    val telState = remember { mutableStateOf("") }
    val errorMessageTelState = remember { mutableStateOf("Enter a valid Tel Number") }
    val shouldShowErrorTelState = remember { mutableStateOf(false) }

    val mailState = remember { mutableStateOf("") }
    val errorMessageMailState = remember { mutableStateOf("Enter a valid Mail") }
    val shouldShowErrorMailState = remember { mutableStateOf(false) }

    val plainTextState = remember { mutableStateOf("") }
    val errorMessagePlainTextState = remember { mutableStateOf("Enter text more than 4 char") }
    val shouldShowErrorPlainTextState = remember { mutableStateOf(false) }

    val urlState = remember { mutableStateOf("") }
    val errorMessageUrlState = remember { mutableStateOf("Enter a valid URL") }
    val shouldShowErrorUrlState = remember { mutableStateOf(false) }

    val geoLatitudeState = remember { mutableStateOf("") }
    val errorMessageGeoLatitudState = remember { mutableStateOf("") }
    val shouldShowErrorGeoLatitudState = remember { mutableStateOf(false) }

    val geoLongitudeState = remember { mutableStateOf("") }
    val errorMessageGeoLongState = remember { mutableStateOf("") }
    val shouldShowErrorGeoLongState = remember { mutableStateOf(false) }

    val eventSubjectState = remember { mutableStateOf("") }
    val errorMessageSubjectState =
        remember { mutableStateOf("The Subject should be more that 4 letters") }
    val shouldShowErrorSubjectState = remember { mutableStateOf(false) }

    val eventDTStartState = remember { mutableStateOf("") }
    val errorMessageEventDTStartState =
        remember { mutableStateOf("add the time data in this format ex time in 24  : 20240622T190000") }
    val shouldShowErrorEventDTStartState = remember { mutableStateOf(false) }

    val eventDTEndState = remember { mutableStateOf("") }
    val errorMessageEventDTEndState =
        remember { mutableStateOf("add the time data in this format ex time in 24  : 20240622T190000") }
    val shouldShowErrorEventDTEndState = remember { mutableStateOf(false) }


    val eventLocationState = remember { mutableStateOf("") }
    val errorMessageEventLocationState =
        remember { mutableStateOf("The Location should be more that 4 letters") }
    val shouldShowErrorEventLocationState = remember { mutableStateOf(false) }
    ///////////////////////// wifi
    val wifiManager =
        LocalContext.current.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager


    val qrBitmapState = remember { mutableStateOf<Bitmap?>(null) }
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
        qrBitmapState.value?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code"
            )
        } ?: Image(
            painter = painterResource(id = android.R.drawable.ic_menu_report_image), // replace with your drawable resource
            contentDescription = "Placeholder"
        )
        Spacer(modifier = Modifier.height(6.dp))

        ////////////////////////////////////
        var expandedDatatype = remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedDatatype.value,
            onExpandedChange = {
                expandedDatatype.value = !expandedDatatype.value
            }
        ) {

            TextField(
                value = pickedTypeState.value,
                onValueChange = { pickedTypeState.value = it },
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
                            pickedTypeState.value = item
                            expandedDatatype.value = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        if (choosenData.value == "WIFI") {
            TextField(
                value = wifiSSIDState.value,
                onValueChange = { newText ->
                    wifiSSIDState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        shouldShowErrorWIFISSIDState.value = newText.isEmpty()
                    }

                },
                label = { Text("Type The number") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorWIFISSIDState.value) {
                Text(text = errorMessageWifiSSIDState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            var expandedEncryptionType = remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedEncryptionType.value,
                onExpandedChange = {
                    expandedEncryptionType.value = !expandedEncryptionType.value
                }
            ) {

                TextField(
                    value = choosenEncryptionState.value,
                    onValueChange = { choosenEncryptionState.value = it },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEncryptionType.value) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedEncryptionType.value,
                    onDismissRequest = {
                        expandedEncryptionType.value = false
                    }
                ) {
                    encryptionWifiTypesList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                choosenWifiEncryptionType.value = item
                                choosenEncryptionState.value = item
                                expandedEncryptionType.value = false
                            }
                        )
                    }
                }
            }
            if (choosenWifiEncryptionType.value != "nopass") {
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = wifiPasswordState.value,
                    onValueChange = { newText ->
                        wifiPasswordState.value = newText

                        coroutineScope.launch(Dispatchers.Default) {
                            delay(3000)
                            shouldShowErrorWifiPasswordState.value = newText.isEmpty()
                        }

                    },
                    label = { Text("Type The number") },
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (shouldShowErrorWifiPasswordState.value) {
                    Text(text = errorMessageWifiPasswordState.value)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
            Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isWifiHiddenState.value, onCheckedChange ={isWifiHiddenState.value = !isWifiHiddenState.value} )
            }

        }

        if (choosenData.value == "Tel") {
            TextField(
                value = telState.value,
                onValueChange = { newText ->
                    telState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        val phoneNumberPattern =
                            Regex("""\+?[0-9]{1,4}?[-.\s]?(\(?\d{1,3}?\)?[-.\s]?){1,4}\d{1,4}""")
                        shouldShowErrorTelState.value = newText.matches(phoneNumberPattern)
                    }

                },
                label = { Text("Type The number") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorTelState.value) {
                Text(text = errorMessageTelState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }

        }
        if (choosenData.value == "SMS") {
            TextField(
                value = smsNumberState.value,
                onValueChange = { newText ->
                    smsNumberState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        val phoneNumberPattern =
                            Regex("""\+?[0-9]{1,4}?[-.\s]?(\(?\d{1,3}?\)?[-.\s]?){1,4}\d{1,4}""")
                        shouldShowErrorSmsNumberState.value = newText.matches(phoneNumberPattern)
                    }

                },
                label = { Text("Type The number") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorSmsNumberState.value) {
                Text(text = errorMessageSmsNumberState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = smsDataState.value,
                onValueChange = { newText ->
                    smsDataState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        shouldShowErrorSmsDataState.value = newText.length < 4
                    }

                },
                label = { Text("Type The number") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorSmsDataState.value) {
                Text(text = errorMessageSmsDataState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        if (choosenData.value == "Mail") {
            TextField(
                value = mailState.value,
                onValueChange = { newText ->
                    mailState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        val emailPattern =
                            Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}""")
                        shouldShowErrorMailState.value = newText.matches(emailPattern)
                    }

                },
                label = { Text("Type The Mail") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorMailState.value) {
                Text(text = errorMessageMailState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }


        }
        if (choosenData.value == "URL") {
            TextField(
                value = urlState.value,
                onValueChange = { newText ->
                    urlState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        val urlPattern =
                            Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""")
                        shouldShowErrorUrlState.value = newText.matches(urlPattern)
                    }

                },
                label = { Text("Type The URL") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorUrlState.value) {
                Text(text = errorMessageUrlState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }


        }

        if (choosenData.value == "Text") {
            TextField(
                value = plainTextState.value,
                onValueChange = { newText ->
                    plainTextState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        shouldShowErrorPlainTextState.value = newText.length < 4
                    }

                },
                label = { Text("Type The Text") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorGeoLatitudState.value) {
                Text(text = errorMessagePlainTextState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }


        }

        if (choosenData.value == "Geo") {
            TextField(
                value = geoLatitudeState.value,
                onValueChange = { newText ->
                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        val regexPattern = Regex("^-?([1-8]?[1-9]|[1-9]0)\\.{0,1}\\d{0,6}$")
                        geoLatitudeState.value = newText

                        shouldShowErrorGeoLatitudState.value =
                            newText.matches(regexPattern)
                    }

                },
                label = { Text("Type The Latitude") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorGeoLatitudState.value) {
                Text(text = errorMessageGeoLatitudState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }

            TextField(
                value = geoLongitudeState.value,
                onValueChange = { newText ->
                    geoLongitudeState.value = newText
                    val regexPattern = Regex("^-?((1?[0-7]?|[0-9]?)[0-9]|180)\\.{0,1}\\d{0,6}$")
                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        geoLongitudeState.value = newText

                        shouldShowErrorGeoLongState.value =
                            newText.matches(regexPattern)
                    }


                },
                label = { Text("Type The Longitude") },
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorGeoLongState.value) {
                Text(text = errorMessageGeoLongState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        if (choosenData.value == "Event") {
            //added edit texts for all its data
            TextField(
                value = eventSubjectState.value,
                onValueChange = { newText ->
                    eventSubjectState.value = newText

                    coroutineScope.launch(Dispatchers.IO) {
                        delay(3000)
                        shouldShowErrorSubjectState.value =
                            newText.isNotEmpty() && newText.length < 4
                    }


                },
                label = { Text("Type The Subject For The Event") },
                placeholder = { Text(text = "ex : Meeting") },
                isError = shouldShowErrorSubjectState.value
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorSubjectState.value) {
                Text(text = errorMessageSubjectState.value)
            }
            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = eventDTStartState.value,
                onValueChange = { newText ->
                    eventDTStartState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        shouldShowErrorEventDTStartState.value =
                            newText.matches(Regex("^\\d{8}T\\d{6}$"))
                    }


                },
                label = { Text("Type The Event Start Time And Date") },
                placeholder = { Text(text = "ex time in 24  : 20240622T190000") },
                isError = shouldShowErrorEventDTStartState.value
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorEventDTStartState.value) {
                Text(text = errorMessageEventDTStartState.value)
                Spacer(modifier = Modifier.height(6.dp))

            }
            TextField(
                value = eventDTEndState.value,
                onValueChange = { newText ->
                    eventDTEndState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        shouldShowErrorEventDTEndState.value =
                            newText.matches(Regex("^\\d{8}T\\d{6}$"))
                    }


                },
                label = { Text("Type The Event End Time And Date") },
                placeholder = { Text(text = "ex time in 24  : 20240622T190000") },
                isError = shouldShowErrorEventDTStartState.value
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorEventDTEndState.value) {
                Text(text = errorMessageEventDTEndState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }
            TextField(
                value = eventLocationState.value,
                onValueChange = { newText ->
                    eventLocationState.value = newText

                    coroutineScope.launch(Dispatchers.Default) {
                        delay(3000)
                        shouldShowErrorEventDTEndState.value =
                            newText.isNotEmpty() && newText.length > 4
                    }


                },
                label = { Text("Entering Location for The Event") },
                placeholder = { Text(text = "ex : Office") },
                isError = shouldShowErrorEventDTStartState.value
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (shouldShowErrorEventLocationState.value) {
                Text(text = errorMessageEventLocationState.value)
                Spacer(modifier = Modifier.height(6.dp))
            }
            // add button for generating

        }
        // for share
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
                        shareImageLauncher.launch(
                            Intent.createChooser(
                                shareIntent,
                                "Share QR Code"
                            )
                        )
                    }
                }
            },
            enabled = qrBitmapState.value != null // Disable the button if qrBitmapState is null
        ) {
            Text("Generate QR Code")
        }
        // for generate
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
                        shareImageLauncher.launch(
                            Intent.createChooser(
                                shareIntent,
                                "Share QR Code"
                            )
                        )
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