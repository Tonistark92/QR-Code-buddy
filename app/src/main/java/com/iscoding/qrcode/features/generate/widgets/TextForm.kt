package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.runtime.Composable
import com.iscoding.qrcode.features.generate.GenerateQRCodeState


@Composable
fun TextInput(plainText : String,
              errorMessagePlainText: String,
              shouldShowErrorPlainText: Boolean,
              updateTextState: (String) -> Unit) {
    MainTextField(
        label = "Type The Text",
        value = plainText,
        errorMessage = errorMessagePlainText,
        shouldShowError = shouldShowErrorPlainText,
        onValueChange = { newText ->
            updateTextState(newText)

        }
    )
}
