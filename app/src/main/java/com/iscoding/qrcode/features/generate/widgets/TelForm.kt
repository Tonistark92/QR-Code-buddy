package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.runtime.Composable

/**
 * Input field for entering a telephone number when generating a QR code.
 *
 * This composable wraps [MainTextField] with predefined label and error handling
 * specific to telephone number inputs.
 *
 * @param tel Current telephone number value of the input field.
 * @param errorMessageTel Error message to display if the input is invalid.
 * @param shouldShowErrorTel Whether the error message should be displayed.
 * @param updateTelState Callback invoked when the telephone number changes.
 */
@Composable
fun TelInput(
    tel: String,
    errorMessageTel: String,
    shouldShowErrorTel: Boolean,
    updateTelState: (String) -> Unit,
) {
    MainTextField(
        label = "Type The number",
        value = tel,
        errorMessage = errorMessageTel,
        shouldShowError = shouldShowErrorTel,
        onValueChange = { newText ->
            updateTelState(newText)
        },
    )
}
