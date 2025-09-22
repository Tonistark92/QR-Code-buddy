package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.runtime.Composable

@Composable
fun URLInput(
    url: String,
    errorMessageUrl: String,
    shouldShowErrorUrl: Boolean,
    updateUrlState: (String) -> Unit,
) {
    MainTextField(
        value = url,
        onValueChange = { newText ->
            updateUrlState(newText)
        },
        label = "Type The URL",
        shouldShowError = shouldShowErrorUrl,
        errorMessage = errorMessageUrl,
    )
}
