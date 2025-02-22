package com.iscoding.qrcode.features.generatecode

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iscoding.qrcode.R
import com.iscoding.qrcode.features.generatecode.widgets.EventInput
import com.iscoding.qrcode.features.generatecode.widgets.GeoInput
import com.iscoding.qrcode.features.generatecode.widgets.MailInput
import com.iscoding.qrcode.features.generatecode.widgets.SmsInput
import com.iscoding.qrcode.features.generatecode.widgets.TelInput
import com.iscoding.qrcode.features.generatecode.widgets.TextInput
import com.iscoding.qrcode.features.generatecode.widgets.URLInput
import kotlinx.coroutines.delay
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
    val dataTypesList = listOf(
        "Text", "URL", "Mail", "Tel", "SMS"
//        , "Geo", "Event"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        if (state.value.isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.size(64.dp),
//                strokeWidth = 6.dp
//            )

//        }

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
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.tertiary, // Only customize this
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface, // Optional
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,  // Optional
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,// Optional
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray,
                    errorLabelColor = Color.Red,

                    ),
                onValueChange = {

                },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
                },

                modifier = Modifier
                    .menuAnchor()
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isExpanded.value,
                onDismissRequest = {
                    isExpanded.value = false

                },
                modifier = Modifier.background(MaterialTheme.colorScheme.secondary)

            ) {
                dataTypesList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
//                            Toast.makeText(
//                                context,
//                                "Dost list",
//                                Toast.LENGTH_SHORT
//                            ).show()
                            state.value.pickedType = item
                            state.value.qrBitmap = null
                            isExpanded.value = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = Color.White,

                            ),
                        modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))

        fun updateState(newState: GenerateQRCodeState) {
            viewModel.updateState(newState)
        }


/// update state and send coroutine
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
//                Log.d("DATA", url + "+++++++++++11111111111")
                updateState(
                    state.value.copy(
                        url = url,
                        shouldShowErrorUrl = showError
                    )
                )
//                Log.d("DATA", state.value.url + "++++++++++++22222222222")

            }

            "Geo" -> {
                GeoInput(state.value, coroutineScope, updateStateLat = { Latitiute ->
                    updateState(
                        state.value.copy(
                            geoLatitude = Latitiute
                        )
                    )

                },
                    updateStateLong = { long ->
                        updateState(
                            state.value.copy(
                                geoLongitude = long
                            )
                        )
                    })
            }

            "Event" -> EventInput(state.value, coroutineScope, updateStateSub = { sub ->
                updateState(
                    state.value.copy(
                        eventSubject = sub
                    )
                )
            },
                updateStateDTStart = { startTimeDate ->
                    updateState(
                        state.value.copy(
                            eventDTStart = startTimeDate
                        )
                    )
                }, updateStateDTEnd = { endTimeDate ->
                    updateState(
                        state.value.copy(
                            eventDTEnd = endTimeDate
                        )
                    )

                },
                updateStateLocation = { location ->

                    updateState(
                        state.value.copy(
                            eventLocation = location
                        )
                    )
                }
            )
        }
        // for share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (state.value.qrBitmap != null) {
                        //  Log.d("QRCode", it.byteCount.toString())
                        val uri = viewModel.getImageUri(context, state.value.qrBitmap!!)
//                   Log.d("QRCode", uri.toString())

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
                    } else {
                        Toast.makeText(
                            context,
                            "You have to generate first",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    state.value.qrBitmap?.let {

                    }
                },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Background color
                    contentColor = Color.White, // Text/Icon color
                    disabledContainerColor = Color.Gray, // Disabled background
                    disabledContentColor = Color.Black // Disabled text color
                )
//            enabled = state.value.qrBitmap != null
            ) {
                Text("Share ")
            }
            // for generate
            Button(
                onClick = {
//                Toast.makeText(context, "Dost", Toast.LENGTH_SHORT).show()
                    val isRightData = viewModel.formatData(state.value.pickedType, state.value)
                    if (isRightData) {
                        state.value.isLoading = true
//                    Log.d("LOADING", "IS Loading in screen    " + "${state.value.isLoading}")
                        when (state.value.pickedType) {
                            "Text" -> {
                                coroutineScope.launch {

                                    viewModel.generateQRCode(
                                        state.value.formattedText,
                                        coroutineScope
                                    )
                                    delay(1000)
                                    viewModel.generateQRCode(
                                        state.value.formattedText,
                                        coroutineScope
                                    )
                                }
                            }

                            "Tel" -> {
                                coroutineScope.launch {


                                    viewModel.generateQRCode(
                                        state.value.formattedTel,
                                        coroutineScope
                                    )
                                    delay(1000)

                                    viewModel.generateQRCode(
                                        state.value.formattedTel,
                                        coroutineScope
                                    )
                                }
                            }

                            "SMS" -> {
                                coroutineScope.launch {


                                    viewModel.generateQRCode(
                                        state.value.formattedSMS,
                                        coroutineScope
                                    )
                                    delay(1000)

                                    viewModel.generateQRCode(
                                        state.value.formattedSMS,
                                        coroutineScope
                                    )
                                }
                            }

                            "Mail" -> {
                                coroutineScope.launch {

                                    viewModel.generateQRCode(

                                        state.value.formattedMail,
                                        coroutineScope
                                    )
                                    delay(1000)

                                    viewModel.generateQRCode(

                                        state.value.formattedMail,
                                        coroutineScope
                                    )
                                }
                            }


                            "URL" -> {
                                coroutineScope.launch {


                                    viewModel.generateQRCode(
                                        state.value.formattedUrl,
                                        coroutineScope
                                    )
                                    delay(1000)

                                    viewModel.generateQRCode(
                                        state.value.formattedUrl,
                                        coroutineScope
                                    )

                                }
                            }


                            "Geo" -> {
                                coroutineScope.launch {


                                    viewModel.generateQRCode(
                                        state.value.formattedGeo,
                                        coroutineScope
                                    )
                                    delay(1000)

                                    viewModel.generateQRCode(
                                        state.value.formattedGeo,
                                        coroutineScope
                                    )
                                }
                            }

                            "Event" -> {
                                coroutineScope.launch {

                                    viewModel.generateQRCode(
                                        state.value.formattedEvent,
                                        coroutineScope
                                    )
                                    delay(1000)

                                    viewModel.generateQRCode(
                                        state.value.formattedEvent,
                                        coroutineScope
                                    )
                                }
                            }
                        }

                    } else {
                        Toast.makeText(
                            context,
                            "Please fit the fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Background color
                    contentColor = Color.White, // Text/Icon color
                    disabledContainerColor = Color.Gray, // Disabled background
                    disabledContentColor = Color.Black // Disabled text color
                )
//            enabled = state.value.qrBitmap != null // Disable the button if qrBitmapState is null
            ) {
                Text("Generate")
            }

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
