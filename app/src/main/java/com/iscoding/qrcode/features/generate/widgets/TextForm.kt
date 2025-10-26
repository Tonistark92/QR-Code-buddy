package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.runtime.Composable

/**
 * Input field for entering plain text when generating a QR code.
 *
 * This composable wraps [MainTextField] with predefined label and error handling
 * specific to plain text inputs.
 *
 * @param plainText Current text value of the input field.
 * @param errorMessagePlainText Error message to display if the input is invalid.
 * @param shouldShowErrorPlainText Whether the error message should be displayed.
 * @param updateTextState Callback invoked when the input text changes.
 */
@Composable
fun TextInput(
    plainText: String,
    errorMessagePlainText: String,
    shouldShowErrorPlainText: Boolean,
    updateTextState: (String) -> Unit,
) {
    MainTextField(
        label = "Type The Text",
        value = plainText,
        errorMessage = errorMessagePlainText,
        shouldShowError = shouldShowErrorPlainText,
        onValueChange = { newText ->
            updateTextState(newText)
        },
    )
}
