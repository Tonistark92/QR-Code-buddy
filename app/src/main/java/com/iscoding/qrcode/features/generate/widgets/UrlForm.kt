package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.runtime.Composable

/**
 * Input field for entering a URL when generating a QR code.
 *
 * This composable wraps [MainTextField] with predefined label and error handling
 * specific to URL inputs.
 *
 * @param url Current text value of the URL input field.
 * @param errorMessageUrl Error message to display if the input is invalid.
 * @param shouldShowErrorUrl Whether the error message should be displayed.
 * @param updateUrlState Callback invoked when the input text changes.
 */
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
