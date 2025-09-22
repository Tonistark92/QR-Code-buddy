package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun SmsInput(
    smsNumber: String,
    smsData: String,
    errorMessageSmsNumber: String,
    errorMessageSmsData: String,
    shouldShowErrorSmsNumber: Boolean,
    shouldShowErrorSmsData: Boolean,
    updateStateSmsNumber: (String) -> Unit,
    updateStateSmsData: (String) -> Unit,
) {
    Column {
        MainTextField(
            label = "Type The number",
            value = smsNumber,
            errorMessage = errorMessageSmsNumber,
            shouldShowError = shouldShowErrorSmsNumber,
            onValueChange = { newText ->
                updateStateSmsNumber(newText)
            },
        )
        MainTextField(
            label = "Type The message",
            value = smsData,
            errorMessage = errorMessageSmsData,
            shouldShowError = shouldShowErrorSmsData,
            onValueChange = { newText ->
                updateStateSmsData(newText)
            },
        )
    }
}
