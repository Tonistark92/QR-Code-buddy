package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iscoding.qrcode.features.generate.GenerateQRCodeState
import kotlinx.coroutines.CoroutineScope


@Composable
fun GeoInput(geoLatitude: String,
             errorMessageGeoLatitude: String,
             shouldShowErrorGeoLatitude: Boolean,
             geoLongitude: String,
             errorMessageGeoLongitude: String,
             shouldShowErrorGeoLongitude: Boolean,
             updateStateLat: (String) -> Unit,
             updateStateLong: (String) -> Unit) {
    MainTextField(
        value = geoLatitude,
        onValueChange = { newText ->

                updateStateLat(newText)
        },
        label = "Type The Latitude",
        shouldShowError =shouldShowErrorGeoLatitude ,
        errorMessage =errorMessageGeoLatitude
    )
    Spacer(modifier = Modifier.height(8.dp))

    MainTextField(
        value = geoLongitude,
        onValueChange = { newText ->
            updateStateLong(newText)
        },
        label = "Type The Longitude",
        shouldShowError =shouldShowErrorGeoLongitude ,
        errorMessage = errorMessageGeoLongitude
    )
}