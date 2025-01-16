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
fun URLInput(state: GenerateQRCodeState, coroutineScope: CoroutineScope) {
    TextField(
        value = state.url,
        onValueChange = { newText ->
            state.url = newText
            coroutineScope.launch(Dispatchers.Default) {
                delay(3000)
                val urlPattern = Regex("""https?://(?:www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)""")
                state.shouldShowErrorUrl = !urlPattern.matches(newText)
            }
        },
        label = { Text("Type The URL") }
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorUrl) {
        Text(text = state.errorMessageUrl, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }
}