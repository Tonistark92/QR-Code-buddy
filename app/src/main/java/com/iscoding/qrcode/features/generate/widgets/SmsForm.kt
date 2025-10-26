package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

/**
 * Input fields for entering an SMS number and message when generating a QR code.
 *
 * This composable wraps two [MainTextField]s inside a [Column]:
 * one for the phone number and one for the message body.
 * Error messages and validation flags are supported for both fields.
 *
 * @param smsNumber Current SMS number value of the input field.
 * @param smsData Current SMS message value of the input field.
 * @param errorMessageSmsNumber Error message to display if the number is invalid.
 * @param errorMessageSmsData Error message to display if the message is invalid.
 * @param shouldShowErrorSmsNumber Whether the number error message should be displayed.
 * @param shouldShowErrorSmsData Whether the message error message should be displayed.
 * @param updateStateSmsNumber Callback invoked when the SMS number changes.
 * @param updateStateSmsData Callback invoked when the SMS message changes.
 */
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
