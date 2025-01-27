package com.iscoding.qrcode.features.generatecode.widgets

import androidx.compose.runtime.Composable
import com.iscoding.qrcode.features.generatecode.GenerateQRCodeState

@Composable
fun TelInput(state: GenerateQRCodeState,
             updateState: (String,Boolean) -> Unit) {
    ValidatedTextField(
        label = "Type The number",
        value = state.tel,
        errorMessage = state.errorMessageTel,
        shouldShowError = state.shouldShowErrorTel,
        onValueChange = { newText ->
            updateState(newText,false)
//            state.shouldShowErrorTel = false
//            updateState(state.copy(tel = newText))
        }
    )
}