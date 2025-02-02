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
fun EventInput(state: GenerateQRCodeState, coroutineScope: CoroutineScope, updateStateSub: (String) -> Unit
, updateStateDTStart: (String) -> Unit,  updateStateDTEnd: (String) -> Unit, updateStateLocation: (String) -> Unit
) {

    ValidatedTextField(
        value = state.eventSubject,
        onValueChange = { newText ->
            state.shouldShowErrorEventSubject = false
//            state.eventSubject = newText
//            coroutineScope.launch(Dispatchers.IO) {
//                delay(3000)
//                state.shouldShowErrorEventSubject = newText.isNotEmpty() && newText.length < 4
                updateStateSub(newText)

//            }
        },
        label = "e.g., Meeting",
        shouldShowError = state.shouldShowErrorEventSubject,
        errorMessage =state.errorMessageEventSubject
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorEventSubject) {
        Text(text = state.errorMessageEventSubject, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }

    ValidatedTextField(
        value = state.eventDTStart,
        onValueChange = { newText ->
            state.shouldShowErrorEventDTStart = false

            state.eventDTStart = newText
//            coroutineScope.launch(Dispatchers.Default) {
//                delay(3000)
//                state.shouldShowErrorEventDTStart = !Regex("^\\d{8}T\\d{6}$").matches(newText)
                updateStateDTStart(newText)

//            }
        },
        label = "Type The Event Start Time And Date",

        shouldShowError = state.shouldShowErrorEventDTStart,
        errorMessage =state.errorMessageEventDTStart
//        placeholder = { Text(text = "e.g., 20240622T190000") },
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorEventDTStart) {
        Text(text = state.errorMessageEventDTStart, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }

    ValidatedTextField(
        value = state.eventDTEnd,
        onValueChange = { newText ->
            state.shouldShowErrorEventDTEnd = false
            state.eventDTEnd = newText
//            coroutineScope.launch(Dispatchers.Default) {
//                delay(3000)
//                state.shouldShowErrorEventDTEnd = !Regex("^\\d{8}T\\d{6}$").matches(newText)
                updateStateDTEnd(newText)

//            }
        },
        label = "Type The Event End Time And Date",
        shouldShowError = state.shouldShowErrorEventDTEnd,
        errorMessage =state.errorMessageEventDTEnd
//        placeholder = { Text(text = "e.g., 20240622T210000") },
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorEventDTEnd) {
        Text(text = state.errorMessageEventDTEnd, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }

    ValidatedTextField(
        value = state.eventLocation,
        onValueChange = { newText ->
            state.shouldShowErrorEventLocation = false
            state.eventLocation = newText
//            coroutineScope.launch(Dispatchers.Default) {
//                delay(3000)
//                state.shouldShowErrorEventLocation = newText.isEmpty() || newText.length <= 4
                updateStateLocation(newText)

//            }
        },
        label = "Type The Location for The Event",
        shouldShowError = state.shouldShowErrorEventLocation,
        errorMessage =state.errorMessageEventLocation
//        placeholder = { Text(text = "e.g., Office") },
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorEventLocation) {
        Text(text = state.errorMessageEventLocation, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }
}