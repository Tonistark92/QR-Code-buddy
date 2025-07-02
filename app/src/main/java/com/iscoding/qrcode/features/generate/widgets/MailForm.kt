package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iscoding.qrcode.features.generate.GenerateQRCodeState


@Composable
fun MailInput(mail: String ,
              errorMessageMail: String,
              shouldShowErrorMail: Boolean,
              updateState: (String) -> Unit) {
    MainTextField(
        value = mail,
        errorMessage = errorMessageMail,
        shouldShowError = shouldShowErrorMail,
        onValueChange = { newText ->

            updateState(newText)

        },
        label = "Type The Mail"
    )
}
