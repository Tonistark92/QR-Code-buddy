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
fun URLInput(url: String,
             errorMessageUrl: String,
             shouldShowErrorUrl: Boolean,
             updateUrlState: (String) -> Unit) {
    MainTextField(
        value = url,
        onValueChange = { newText ->
            updateUrlState(newText)
        },
        label = "Type The URL",
        shouldShowError =shouldShowErrorUrl ,
        errorMessage = errorMessageUrl
    )
}