package com.iscoding.qrcode.features.generatecode.widgets

import android.util.Log
import androidx.compose.runtime.Composable
import com.iscoding.qrcode.features.generatecode.GenerateQRCodeState


@Composable
fun TextInput(state: GenerateQRCodeState, updateState: (GenerateQRCodeState) -> Unit) {
    ValidatedTextField(
        label = "Type The Text",
        value = state.plainText,
        errorMessage = state.errorMessagePlainText,
        shouldShowError = state.shouldShowErrorPlainText,
        onValueChange = { newText ->
            Log.d("ISLAM",newText+"IN Form//////////////")
            updateState(state.copy(plainText = newText))
            Log.d("ISLAM",state.plainText+"IN Form STATE//////////////")

        }
    )
}
