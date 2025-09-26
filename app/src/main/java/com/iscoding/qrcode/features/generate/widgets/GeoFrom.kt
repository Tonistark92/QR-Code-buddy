package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A reusable input component for entering geographical coordinates.
 *
 * This composable provides two text fields:
 * - One for **Latitude**
 * - One for **Longitude**
 *
 * Each field supports validation by showing error messages
 * when the respective `shouldShowError` flag is set to true.
 *
 * @param geoLatitude The current latitude value.
 * @param errorMessageGeoLatitude The error message to show for the latitude input.
 * @param shouldShowErrorGeoLatitude Whether to display the latitude error message.
 * @param geoLongitude The current longitude value.
 * @param errorMessageGeoLongitude The error message to show for the longitude input.
 * @param shouldShowErrorGeoLongitude Whether to display the longitude error message.
 * @param updateStateLat Callback triggered when the latitude value changes.
 * @param updateStateLong Callback triggered when the longitude value changes.
 */
@Composable
fun GeoInput(
    geoLatitude: String,
    errorMessageGeoLatitude: String,
    shouldShowErrorGeoLatitude: Boolean,
    geoLongitude: String,
    errorMessageGeoLongitude: String,
    shouldShowErrorGeoLongitude: Boolean,
    updateStateLat: (String) -> Unit,
    updateStateLong: (String) -> Unit,
) {
    MainTextField(
        value = geoLatitude,
        onValueChange = { newText ->
            updateStateLat(newText)
        },
        label = "Type The Latitude",
        shouldShowError = shouldShowErrorGeoLatitude,
        errorMessage = errorMessageGeoLatitude,
    )
    Spacer(modifier = Modifier.height(8.dp))

    MainTextField(
        value = geoLongitude,
        onValueChange = { newText ->
            updateStateLong(newText)
        },
        label = "Type The Longitude",
        shouldShowError = shouldShowErrorGeoLongitude,
        errorMessage = errorMessageGeoLongitude,
    )
}
