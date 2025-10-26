package com.iscoding.qrcode.features.generate

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iscoding.qrcode.R
import com.iscoding.qrcode.features.generate.event.GenerateQRCodeUiEvent
import com.iscoding.qrcode.features.generate.event.GenerateQrEvent
import com.iscoding.qrcode.features.generate.util.QrDataType
import com.iscoding.qrcode.features.generate.widgets.EventInput
import com.iscoding.qrcode.features.generate.widgets.GeoInput
import com.iscoding.qrcode.features.generate.widgets.MailInput
import com.iscoding.qrcode.features.generate.widgets.ShimmerImage
import com.iscoding.qrcode.features.generate.widgets.SmsInput
import com.iscoding.qrcode.features.generate.widgets.TelInput
import com.iscoding.qrcode.features.generate.widgets.TextInput
import com.iscoding.qrcode.features.generate.widgets.URLInput
import com.iscoding.qrcode.features.util.getImageUri
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQRCodeScreen() {
    // Get ViewModel from Koin
    val viewModel = koinViewModel<GenerateQRCodeViewModel>()

    // Observe state with lifecycle awareness
    val state = viewModel.state.collectAsStateWithLifecycle()

    // Launcher for sharing the QR image
    val shareImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    val context = LocalContext.current
    // Available QR data types
    val dataTypesList = QrDataType.entries

    // Collect one-time UI events (sharing, toast messages, etc.)
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is GenerateQRCodeUiEvent.RequestShare -> {
                    // Get content URI for the generated QR bitmap
                    val uri = getImageUri(context, event.bitmap)
                    if (uri != null) {
                        // Build system share intent
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, uri)
                            type = "image/png"
                        }
                        shareImageLauncher.launch(
                            Intent.createChooser(shareIntent, "Share QR Code"),
                        )
                    } else {
                        Toast.makeText(context, "Could not get image URI", Toast.LENGTH_SHORT).show()
                    }
                }

                is GenerateQRCodeUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Main screen container
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // --- QR Code Preview Section ---
            Box(
                modifier = Modifier.height(200.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                if (state.value.qrBitmap != null) {
                    // Show generated QR bitmap
                    Image(
                        bitmap = state.value.qrBitmap!!.asImageBitmap(),
                        contentDescription = "QR Code",
                    )
                } else {
                    // Show shimmer placeholder when no QR is generated
                    ShimmerImage(isLoading = state.value.isLoading) {
                        Image(
                            painter = painterResource(id = R.drawable.no_pictures),
                            contentDescription = "Placeholder",
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(25.dp))

            // --- Dropdown Menu for selecting QR data type ---
            val isExpanded = remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = isExpanded.value,
                onExpandedChange = { isExpanded.value = !isExpanded.value },
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            ) {
                // Display selected QR type
                TextField(
                    value = context.getString(state.value.pickedType.labelResId),
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Gray,
                        errorLabelColor = Color.Red,
                    ),
                    modifier = Modifier.menuAnchor().background(MaterialTheme.colorScheme.primary).fillMaxWidth(),
                )

                // Dropdown items = all data types
                ExposedDropdownMenu(
                    expanded = isExpanded.value,
                    onDismissRequest = { isExpanded.value = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
                ) {
                    dataTypesList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(context.getString(item.labelResId)) },
                            onClick = {
                                // Update state when new type is picked
                                state.value.pickedType = item
                                state.value.qrBitmap = null // Reset QR preview
                                isExpanded.value = false
                            },
                            colors = MenuDefaults.itemColors(textColor = Color.White),
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // --- Dynamic Input Section ---
            // Show different input form depending on selected type
            when (state.value.pickedType) {
                QrDataType.TEXT ->
                    TextInput(
                        plainText = state.value.plainText,
                        errorMessagePlainText = state.value.errorMessagePlainText,
                        shouldShowErrorPlainText = state.value.shouldShowErrorPlainText,
                    ) { text ->
                        viewModel.onEvent(GenerateQrEvent.OnTextChanged(text))
                    }

                QrDataType.TEL ->
                    TelInput(
                        tel = state.value.tel,
                        errorMessageTel = state.value.errorMessageTel,
                        shouldShowErrorTel = state.value.shouldShowErrorTel,
                    ) { tel ->
                        viewModel.onEvent(GenerateQrEvent.OnTelChanged(tel))
                    }

                QrDataType.SMS ->
                    SmsInput(
                        smsNumber = state.value.smsNumber,
                        smsData = state.value.smsData,
                        errorMessageSmsData = state.value.errorMessageSmsData,
                        errorMessageSmsNumber = state.value.errorMessageSmsNumber,
                        shouldShowErrorSmsData = state.value.shouldShowErrorSmsData,
                        shouldShowErrorSmsNumber = state.value.shouldShowErrorSmsNumber,
                        updateStateSmsData = { message ->
                            viewModel.onEvent(GenerateQrEvent.OnSmsMessageChanged(message))
                        },
                        updateStateSmsNumber = { number ->
                            viewModel.onEvent(GenerateQrEvent.OnSmsNumberChanged(number))
                        },
                    )

                QrDataType.MAIL ->
                    MailInput(
                        mail = state.value.mail,
                        errorMessageMail = state.value.errorMessageMail,
                        shouldShowErrorMail = state.value.shouldShowErrorMail,
                    ) { mail ->
                        viewModel.onEvent(GenerateQrEvent.OnMailChanged(mail))
                    }

                QrDataType.URL ->
                    URLInput(
                        url = state.value.url,
                        errorMessageUrl = state.value.errorMessageUrl,
                        shouldShowErrorUrl = state.value.shouldShowErrorUrl,
                    ) { url ->
                        viewModel.onEvent(GenerateQrEvent.OnUrlChanged(url))
                    }

                QrDataType.GEO ->
                    GeoInput(
                        geoLatitude = state.value.geoLatitude,
                        errorMessageGeoLatitude = state.value.errorMessageGeoLatitude,
                        shouldShowErrorGeoLatitude = state.value.shouldShowErrorGeoLatitude,
                        geoLongitude = state.value.geoLongitude,
                        errorMessageGeoLongitude = state.value.errorMessageGeoLongitude,
                        shouldShowErrorGeoLongitude = state.value.shouldShowErrorGeoLongitude,
                        updateStateLat = { latitude ->
                            viewModel.onEvent(GenerateQrEvent.OnGeoLatitudeChanged(latitude))
                        },
                        updateStateLong = { longitude ->
                            viewModel.onEvent(GenerateQrEvent.OnGeoLongitudeChanged(longitude))
                        },
                    )

                QrDataType.EVENT ->
                    EventInput(
                        eventSubject = state.value.eventSubject,
                        errorMessageEventSubject = state.value.errorMessageEventSubject,
                        shouldShowErrorEventSubject = state.value.shouldShowErrorEventSubject,
                        eventDTStart = state.value.eventDTStart,
                        errorMessageEventDTStart = state.value.errorMessageEventDTStart,
                        shouldShowErrorEventDTStart = state.value.shouldShowErrorEventDTStart,
                        eventDTEnd = state.value.eventDTEnd,
                        errorMessageEventDTEnd = state.value.errorMessageEventDTEnd,
                        shouldShowErrorEventDTEnd = state.value.shouldShowErrorEventDTEnd,
                        eventLocation = state.value.eventLocation,
                        errorMessageEventLocation = state.value.errorMessageEventLocation,
                        shouldShowErrorEventLocation = state.value.shouldShowErrorEventLocation,
                        updateStateSub = { subject ->
                            viewModel.onEvent(GenerateQrEvent.OnEventSubjectChanged(subject))
                        },
                        updateStateDTStart = { start ->
                            viewModel.onEvent(GenerateQrEvent.OnEventDTStartChanged(start))
                        },
                        updateStateDTEnd = { end ->
                            viewModel.onEvent(GenerateQrEvent.OnEventDTEndChanged(end))
                        },
                        updateStateLocation = { location ->
                            viewModel.onEvent(GenerateQrEvent.OnEventLocationChanged(location))
                        },
                    )
            }

            // --- Action Buttons ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Share button (only works if QR exists)
                Button(
                    onClick = {
                        if (state.value.qrBitmap != null) {
                            viewModel.onEvent(GenerateQrEvent.ShareQRCode)
                        }
                    },
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Black,
                    ),
                ) {
                    Text("Share")
                }

                // Generate QR button
                Button(
                    onClick = {
                        viewModel.onEvent(GenerateQrEvent.GenerateQRCode)
                    },
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Black,
                    ),
                ) {
                    Text("Generate")
                }
            }
        }
    }
}

// // Assume the user entered this date-time string
// val userEnteredDateTimeString = "20240622T190000"
//
// // Parse the user-entered date-time string
// val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
// val localDateTime = LocalDateTime.parse(userEnteredDateTimeString, formatter)
//
// // Get the user's local time zone
// val userZoneId = ZoneId.systemDefault()
//
// // Convert LocalDateTime to ZonedDateTime with user's local time zone
// val zonedDateTime = localDateTime.atZone(userZoneId)
//
// // Format the ZonedDateTime to a string including the time zone info
// val formattedDateTime = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a z"))
//
// // Print the formatted date-time string
// println("User's local date-time: $formattedDateTime")
//
// // Additionally, convert the ZonedDateTime to UTC and format it
// val utcZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"))
// val formattedUtcDateTime = utcZonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
//
// // Print the formatted UTC date-time string
// println("UTC date-time: $formattedUtcDateTime")
