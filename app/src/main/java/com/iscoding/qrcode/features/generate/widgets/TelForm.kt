package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.runtime.Composable
import com.iscoding.qrcode.features.generate.GenerateQRCodeState

@Composable
fun TelInput( tel: String,
              errorMessageTel: String,
              shouldShowErrorTel: Boolean,
              updateTelState: (String) -> Unit) {
    MainTextField(
        label = "Type The number",
        value = tel,
        errorMessage = errorMessageTel,
        shouldShowError = shouldShowErrorTel,
        onValueChange = { newText ->
            updateTelState(newText)
        }
    )
}