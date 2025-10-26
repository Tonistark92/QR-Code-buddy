package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.runtime.Composable

/**
 * A reusable input field for email addresses.
 *
 * This composable wraps [MainTextField] and applies a predefined
 * label ("Type The Mail") to keep consistency for email inputs.
 *
 * It also supports validation by showing an error message when
 * [shouldShowErrorMail] is true.
 *
 * @param mail The current value of the email input field.
 * @param errorMessageMail The error message to display when validation fails.
 * @param shouldShowErrorMail Whether the error message should be visible.
 * @param updateState Callback triggered when the email value changes.
 */
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
