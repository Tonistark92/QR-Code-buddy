package com.iscoding.qrcode.features.generatecode

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.iscoding.qrcode.features.generatecode.widgets.EventInput
import com.iscoding.qrcode.features.generatecode.widgets.GeoInput
import com.iscoding.qrcode.features.generatecode.widgets.MailInput
import com.iscoding.qrcode.features.generatecode.widgets.SmsInput
import com.iscoding.qrcode.features.generatecode.widgets.TelInput
import com.iscoding.qrcode.features.generatecode.widgets.TextInput
import com.iscoding.qrcode.features.generatecode.widgets.URLInput
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ToDo: Formate the data for generating the qr
// ToDo: create Button for each case
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQRCode() {


//    val viewmodel = getViewModel<GenerateQRCodeViewModel>()
    val viewModel = koinViewModel<GenerateQRCodeViewModel>()
    val state = viewModel.state.collectAsState()


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
                viewModel.updateState(newState)
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
        // for share
        Button(
            onClick = {
                state.value.qrBitmap?.let {
//                    Log.d("QRCode", it.byteCount.toString())
                    val uri =viewModel. getImageUri(context, it)
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
            enabled = state.value.qrBitmap != null
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
                    val isRightData = viewModel.formatData(choosenData.value,state.value)
                    if (isRightData) {
                        val uri = it?.let { it1 -> viewModel. getImageUri(context, it1) }
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
