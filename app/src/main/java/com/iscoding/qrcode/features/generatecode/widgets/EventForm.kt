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
fun EventInput(state: GenerateQRCodeState, coroutineScope: CoroutineScope) {
    TextField(
        value = state.eventSubject,
        onValueChange = { newText ->
            state.eventSubject = newText
            coroutineScope.launch(Dispatchers.IO) {
                delay(3000)
                state.shouldShowErrorEventSubject = newText.isNotEmpty() && newText.length < 4
            }
        },
        label = { Text("Type The Subject For The Event") },
        placeholder = { Text(text = "e.g., Meeting") },
        isError = state.shouldShowErrorEventSubject
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorEventSubject) {
        Text(text = state.errorMessageEventSubject, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }

    TextField(
        value = state.eventDTStart,
        onValueChange = { newText ->
            state.eventDTStart = newText
            coroutineScope.launch(Dispatchers.Default) {
                delay(3000)
                state.shouldShowErrorEventDTStart = !Regex("^\\d{8}T\\d{6}$").matches(newText)
            }
        },
        label = { Text("Type The Event Start Time And Date") },
        placeholder = { Text(text = "e.g., 20240622T190000") },
        isError = state.shouldShowErrorEventDTStart
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorEventDTStart) {
        Text(text = state.errorMessageEventDTStart, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }

    TextField(
        value = state.eventDTEnd,
        onValueChange = { newText ->
            state.eventDTEnd = newText
            coroutineScope.launch(Dispatchers.Default) {
                delay(3000)
                state.shouldShowErrorEventDTEnd = !Regex("^\\d{8}T\\d{6}$").matches(newText)
            }
        },
        label = { Text("Type The Event End Time And Date") },
        placeholder = { Text(text = "e.g., 20240622T210000") },
        isError = state.shouldShowErrorEventDTEnd
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorEventDTEnd) {
        Text(text = state.errorMessageEventDTEnd, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }

    TextField(
        value = state.eventLocation,
        onValueChange = { newText ->
            state.eventLocation = newText
            coroutineScope.launch(Dispatchers.Default) {
                delay(3000)
                state.shouldShowErrorEventLocation = newText.isEmpty() || newText.length <= 4
            }
        },
        label = { Text("Type The Location for The Event") },
        placeholder = { Text(text = "e.g., Office") },
        isError = state.shouldShowErrorEventLocation
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorEventLocation) {
        Text(text = state.errorMessageEventLocation, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }
}