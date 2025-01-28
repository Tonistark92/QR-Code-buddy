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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.iscoding.qrcode.R
import com.iscoding.qrcode.features.generatecode.widgets.EventInput
import com.iscoding.qrcode.features.generatecode.widgets.GeoInput
import com.iscoding.qrcode.features.generatecode.widgets.MailInput
import com.iscoding.qrcode.features.generatecode.widgets.SmsInput
import com.iscoding.qrcode.features.generatecode.widgets.TelInput
import com.iscoding.qrcode.features.generatecode.widgets.TextInput
import com.iscoding.qrcode.features.generatecode.widgets.URLInput
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQRCodeScreen() {


    val viewModel = koinViewModel<GenerateQRCodeViewModel>()
    val state = viewModel.state.collectAsState()


    val coroutineScope = rememberCoroutineScope()
    val shareImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    val context = LocalContext.current
    val dataTypesList = listOf("URL", "Mail", "Tel", "SMS", "Geo", "Event", "Text")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.value.isLoading) {
//            Spacer(modifier = Modifier.height(25.dp))
//
////            CircularProgressIndicator(color = Color.Black)
////            CircularProgressIndicator(
////                modifier = Modifier.size(64.dp),
////                strokeWidth = 6.dp
////            )
//            Text("Loaddding") //todo CircularProgressIndicator crash i replaced it by text to trace
//            Spacer(modifier = Modifier.height(25.dp))

        }

        if (state.value.qrBitmap != null) {
            Image(
                bitmap = state.value.qrBitmap!!.asImageBitmap(),
                contentDescription = "QR Code"
            )
//            Toast.makeText(
//                context,
//                "Generated",
//                Toast.LENGTH_SHORT
//            ).show()
        } else {
            Image(
                painter = painterResource(id = R.drawable.no_pictures),
                contentDescription = "Placeholder"
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        val isExpanded = remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = isExpanded.value,
            onExpandedChange = {
                isExpanded.value = !isExpanded.value
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            TextField(
                value = state.value.pickedType,
                onValueChange = {

                },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = {
                    isExpanded.value = false

                }
            ) {
                dataTypesList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            Toast.makeText(
                                context,
                                "Dost list",
                                Toast.LENGTH_SHORT
                            ).show()
                            state.value.pickedType = item
                            state.value.qrBitmap = null
                            isExpanded.value = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))

        fun updateState(newState: GenerateQRCodeState) {
                viewModel.updateState(newState)
        }


/// update state and send corotien
        when (state.value.pickedType) {
            "Text" -> TextInput(state.value) { text ->
                updateState(
                    state.value.copy(
                        plainText = text
                    )
                )
            }

            "Tel" -> TelInput(state.value) { tel, error ->
                updateState(
                    state.value.copy(
                        tel = tel,
                        shouldShowErrorTel = error
                    )
                )
            }

            "SMS" -> SmsInput(state.value, updateStateSmsData = { data ->
                updateState(
                    state.value.copy(
                        smsData = data
                    )
                )
            }, updateStateSmsNumber = { number ->
                updateState(
                    state.value.copy(
                        smsNumber = number
                    )
                )
            })

            "Mail" -> MailInput(state.value) { mail, showError ->
                updateState(
                    state.value.copy(
                        mail = mail,
                        shouldShowErrorMail = showError
                    )
                )
            }


            "URL" -> URLInput(state.value) { url, showError ->
                Log.d("DATA",url + "+++++++++++11111111111")
                updateState(
                    state.value.copy (
                        url = url,
                        shouldShowErrorUrl = showError
                    )
                )
                Log.d("DATA",state.value.url + "++++++++++++22222222222")

            }

            "Geo" -> GeoInput(state.value, coroutineScope, ::updateState)
            "Event" -> EventInput(state.value, coroutineScope, ::updateState)
        }
        // for share
        Button(
            onClick = {
                state.value.qrBitmap?.let {
//                    Log.d("QRCode", it.byteCount.toString())
                    val uri = viewModel.getImageUri(context, it)
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
//            enabled = state.value.qrBitmap != null
        ) {
            Text("Share QR Code")
        }
        // for generate
        Button(
            onClick = {
                Toast.makeText(context, "Dost", Toast.LENGTH_SHORT).show()
                val isRightData = viewModel.formatData(state.value.pickedType, state.value)
                if (isRightData) {
                    state.value.isLoading = true
                    Log.d("LOADING", "IS Loading in screen    " + "${state.value.isLoading}")
                    when (state.value.pickedType) {
                        "Text" ->  viewModel.generateQRCode(state.value.formattedText, coroutineScope)

                        "Tel" -> viewModel.generateQRCode(state.value.formattedTel, coroutineScope)

                        "SMS" ->  viewModel.generateQRCode(state.value.formattedSMS, coroutineScope)

                        "Mail" -> viewModel.generateQRCode(state.value.formattedMail, coroutineScope)


                        "URL" ->  viewModel.generateQRCode(state.value.formattedUrl, coroutineScope)


                        "Geo" ->  viewModel.generateQRCode(state.value.formattedGeo, coroutineScope)
                        "Event" ->    viewModel.generateQRCode(state.value.formattedEvent, coroutineScope)

                    }

                } else {
                    Toast.makeText(
                        context,
                        "Please fit the mentioned examples",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
//            enabled = state.value.qrBitmap != null // Disable the button if qrBitmapState is null
        ) {
            Text("Generate QR Code")
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
