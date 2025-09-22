package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.runtime.Composable

@Composable
fun MailInput(
    mail: String,
    errorMessageMail: String,
    shouldShowErrorMail: Boolean,
    updateState: (String) -> Unit,
) {
    MainTextField(
        value = mail,
        errorMessage = errorMessageMail,
        shouldShowError = shouldShowErrorMail,
        onValueChange = { newText ->

            updateState(newText)
        },
        label = "Type The Mail",
    )
}
