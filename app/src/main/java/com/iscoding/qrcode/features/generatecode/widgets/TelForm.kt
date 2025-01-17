package com.iscoding.qrcode.features.generatecode.widgets

import androidx.compose.runtime.Composable
import com.iscoding.qrcode.features.generatecode.GenerateQRCodeState

@Composable
fun TelInput(state: GenerateQRCodeState, updateState: (GenerateQRCodeState) -> Unit) {
    ValidatedTextField(
        label = "Type The number",
        value = state.tel,
        errorMessage = state.errorMessageTel,
        shouldShowError = state.shouldShowErrorTel,
        onValueChange = { newText ->
            updateState(state.copy(tel = newText))
        }
    )
}