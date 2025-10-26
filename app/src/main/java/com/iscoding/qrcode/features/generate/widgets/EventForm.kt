package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A reusable input component for creating an event form.
 *
 * This composable provides four text fields:
 * - **Event Subject** (e.g., Meeting)
 * - **Event Start Date/Time**
 * - **Event End Date/Time**
 * - **Event Location**
 *
 * Each field supports validation by showing an error message
 * when the corresponding `shouldShowError` flag is set to true.
 *
 * @param eventSubject The current subject of the event.
 * @param errorMessageEventSubject The error message for the subject input.
 * @param shouldShowErrorEventSubject Whether to display the subject error message.
 *
 * @param eventDTStart The current start date/time of the event.
 * @param errorMessageEventDTStart The error message for the start date/time input.
 * @param shouldShowErrorEventDTStart Whether to display the start date/time error message.
 *
 * @param eventDTEnd The current end date/time of the event.
 * @param errorMessageEventDTEnd The error message for the end date/time input.
 * @param shouldShowErrorEventDTEnd Whether to display the end date/time error message.
 *
 * @param eventLocation The current location of the event.
 * @param errorMessageEventLocation The error message for the location input.
 * @param shouldShowErrorEventLocation Whether to display the location error message.
 *
 * @param updateStateSub Callback triggered when the subject value changes.
 * @param updateStateDTStart Callback triggered when the start date/time changes.
 * @param updateStateDTEnd Callback triggered when the end date/time changes.
 * @param updateStateLocation Callback triggered when the location changes.
 */
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
    // Event subject input
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

    // Event start datetime input
    MainTextField(
        value = eventDTStart,
        onValueChange = { newText ->
            updateStateDTStart(newText)
        },
        label = "Type The Event Start Time And Date",
        shouldShowError = shouldShowErrorEventDTStart,
        errorMessage = errorMessageEventDTStart,
        // Example format: 20240622T190000
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Event end datetime input
    MainTextField(
        value = eventDTEnd,
        onValueChange = { newText ->
            updateStateDTEnd(newText)
        },
        label = "Type The Event End Time And Date",
        shouldShowError = shouldShowErrorEventDTEnd,
        errorMessage = errorMessageEventDTEnd,
        // Example format: 20240622T210000
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Event location input
    MainTextField(
        value = eventLocation,
        onValueChange = { newText ->
            updateStateLocation(newText)
        },
        label = "Type The Location for The Event",
        shouldShowError = shouldShowErrorEventLocation,
        errorMessage = errorMessageEventLocation,
        // Example: Office
    )

    Spacer(modifier = Modifier.height(8.dp))
}
