package com.iscoding.qrcode.features.generatecode.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iscoding.qrcode.features.generatecode.GenerateQRCodeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun GeoInput(state: GenerateQRCodeState, coroutineScope: CoroutineScope, updateStateLat: (String) -> Unit,updateStateLong: (String) -> Unit) {
    ValidatedTextField(
        value = state.geoLatitude,
        onValueChange = { newText ->
            state.shouldShowErrorGeoLatitude = false

            state.geoLatitude = newText
//            coroutineScope.launch(Dispatchers.Default) {
//                delay(3000)
//                val regexPattern = Regex("^-?([1-8]?[1-9]|[1-9]0)\\.{0,1}\\d{0,6}$")
//                state.shouldShowErrorGeoLatitude = !regexPattern.matches(newText)
                updateStateLat(newText)

//            }
        },
        label = "Type The Latitude",
        shouldShowError =state.shouldShowErrorGeoLatitude ,
        errorMessage =state.errorMessageGeoLongitude
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorGeoLatitude) {
        Text(text = state.errorMessageGeoLatitude, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }

    ValidatedTextField(
        value = state.geoLongitude,
        onValueChange = { newText ->
            state.shouldShowErrorGeoLongitude = false
            state.geoLongitude = newText
//            coroutineScope.launch(Dispatchers.Default) {
//                delay(3000)
//                val regexPattern = Regex("^-?((1?[0-7]?|[0-9]?)[0-9]|180)\\.{0,1}\\d{0,6}$")
//                state.shouldShowErrorGeoLongitude = !regexPattern.matches(newText)
//                updateState(state.copy(geoLongitude = newText))
//            }
            updateStateLong(newText)

        },
        label = "Type The Longitude",
        shouldShowError =state.shouldShowErrorGeoLongitude ,
        errorMessage = state.errorMessageGeoLongitude
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorGeoLongitude) {
        Text(text = state.errorMessageGeoLongitude, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }
}