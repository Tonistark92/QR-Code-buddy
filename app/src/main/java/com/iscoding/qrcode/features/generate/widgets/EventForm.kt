package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EventInput(
    eventSubject: String,
    errorMessageEventSubject: String,
    shouldShowErrorEventSubject: Boolean,
    eventDTStart: String,
    errorMessageEventDTStart: String,
    shouldShowErrorEventDTStart: Boolean,
    eventDTEnd: String,
    errorMessageEventDTEnd: String,
    shouldShowErrorEventDTEnd: Boolean,
    eventLocation: String,
    errorMessageEventLocation: String,
    shouldShowErrorEventLocation: Boolean,
    updateStateSub: (String) -> Unit,
    updateStateDTStart: (String) -> Unit,
    updateStateDTEnd: (String) -> Unit,
    updateStateLocation: (String) -> Unit,
) {
    MainTextField(
        value = eventSubject,
        onValueChange = { newText ->
            updateStateSub(newText)
        },
        label = "e.g., Meeting",
        shouldShowError = shouldShowErrorEventSubject,
        errorMessage = errorMessageEventSubject,
    )

    Spacer(modifier = Modifier.height(8.dp))

    MainTextField(
        value = eventDTStart,
        onValueChange = { newText ->

            updateStateDTStart(newText)
        },
        label = "Type The Event Start Time And Date",
        shouldShowError = shouldShowErrorEventDTStart,
        errorMessage = errorMessageEventDTStart,
//        placeholder = { Text(text = "e.g., 20240622T190000") },
    )
    Spacer(modifier = Modifier.height(8.dp))

    MainTextField(
        value = eventDTEnd,
        onValueChange = { newText ->
            updateStateDTEnd(newText)
        },
        label = "Type The Event End Time And Date",
        shouldShowError = shouldShowErrorEventDTEnd,
        errorMessage = errorMessageEventDTEnd,
//        placeholder = { Text(text = "e.g., 20240622T210000") },
    )
    Spacer(modifier = Modifier.height(8.dp))

    MainTextField(
        value = eventLocation,
        onValueChange = { newText ->
            updateStateLocation(newText)
        },
        label = "Type The Location for The Event",
        shouldShowError = shouldShowErrorEventLocation,
        errorMessage = errorMessageEventLocation,
//        placeholder = { Text(text = "e.g., Office") },
    )
    Spacer(modifier = Modifier.height(8.dp))
}
