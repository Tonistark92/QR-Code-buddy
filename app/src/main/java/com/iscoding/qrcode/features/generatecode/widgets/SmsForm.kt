package com.iscoding.qrcode.features.generatecode.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.iscoding.qrcode.features.generatecode.GenerateQRCodeState


@Composable
fun SmsInput(state: GenerateQRCodeState, updateState: (GenerateQRCodeState) -> Unit) {
    Column {
        ValidatedTextField(
            label = "Type The number",
            value = state.smsNumber,
            errorMessage = state.errorMessageSmsNumber,
            shouldShowError = state.shouldShowErrorSmsNumber,
            onValueChange = { newText ->
                updateState(state.copy(smsNumber = newText))
            }
        )
        ValidatedTextField(
            label = "Type The message",
            value = state.smsData,
            errorMessage = state.errorMessageSmsData,
            shouldShowError = state.shouldShowErrorSmsData,
            onValueChange = { newText ->
                updateState(state.copy(smsData = newText))
            }
        )
    }
}