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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import com.iscoding.qrcode.features.generatecode.widgets.TextInput
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
        if (state.value.isLoading){
            Spacer(modifier = Modifier.height(25.dp))

            CircularProgressIndicator(color = Color.Black)
            Spacer(modifier = Modifier.height(25.dp))

        }

        if (state.value.qrBitmap != null){
            Image(
                bitmap = state.value.qrBitmap!!.asImageBitmap(),
                contentDescription = "QR Code"
            )
            Toast.makeText(
                context,
                "Generated",
                Toast.LENGTH_SHORT
            ).show()
        }
        else{
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_report_image),
                contentDescription = "Placeholder"
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

//        val isExpanded = remember { mutableStateOf(false) }
//
//        ExposedDropdownMenuBox(
//            expanded = isExpanded.value,
//            onExpandedChange = {
//                isExpanded.value = !isExpanded.value
//            },
//            modifier = Modifier.fillMaxWidth().wrapContentHeight()
//        ) {
//
//            TextField(
//                value = state.value.pickedType,
//                onValueChange = {  },
//                readOnly = true,
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded.value) },
//                modifier = Modifier.menuAnchor()
//            )
//
//            ExposedDropdownMenu(
//                expanded = isExpanded.value,
//                onDismissRequest = {
//                    isExpanded.value = false
//                }
//            ) {
//                dataTypesList.forEach { item ->
//                    DropdownMenuItem(
//                        text = { Text(item) },
//                        onClick = {
//                            choosenData.value = item
//                            state.value.pickedType = item
//                            state.value.qrBitmap = null
//                            isExpanded.value = false
//                        }
//                    )
//                }
//            }
//        }
        Spacer(modifier = Modifier.height(26.dp))

        fun updateState(newState: GenerateQRCodeState) {
            coroutineScope.launch {
                viewModel.updateState(newState)
            }
        }
        TextInput(state.value, ::updateState)
/// update state and send corotien
//        when (state.value.pickedType) {
//            "Text" -> TextInput(state.value, ::updateState)
//            "Tel" -> TelInput(state.value, ::updateState)
//            "SMS" -> SmsInput(state.value, ::updateState)
//            "Mail" -> MailInput(state.value, coroutineScope, ::updateState)
//            "URL" -> URLInput(state.value, coroutineScope,::updateState)
//            "Geo" -> GeoInput(state.value, coroutineScope, ::updateState)
//            "Event" -> EventInput(state.value, coroutineScope, ::updateState)
//        }
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
                val isRightData = viewModel.formatData("Text", state.value)
                if (isRightData) {
                    state.value.isLoading = true
                    Log.d("LOADING", "IS Loading in screen    "+"${state.value.isLoading}")
                    viewModel.generateQRCode(state.value.formattedText, coroutineScope)

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
