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
fun MailInput(state: GenerateQRCodeState,
              updateState: (String,Boolean) -> Unit) {
    ValidatedTextField(
        value = state.mail,
        errorMessage = state.errorMessagePlainText,
        shouldShowError = state.shouldShowErrorPlainText,
        onValueChange = { newText ->
//            state.shouldShowErrorMail= false
//            state.mail = newText
//            coroutineScope.launch(Dispatchers.Default) {
//                delay(3000)
            val emailPattern = Regex("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}""")
            state.shouldShowErrorMail = !emailPattern.matches(newText)


            updateState(newText,!emailPattern.matches(newText))
//            }
//            updateState(state.copy(mail = newText))

        },
        label = "Type The Mail"
    )
    Spacer(modifier = Modifier.height(6.dp))
    if (state.shouldShowErrorMail) {
        Text(text = state.errorMessageMail, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(6.dp))
    }
}
