package com.iscoding.qrcode.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iscoding.qrcode.theme.QrCodeBuddyTheme
import com.iscoding.qrcode.theme.Theme

@Composable
fun QRBuddyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = Theme.typography.body.copy(
                color = Theme.colors.onSurface
            ),
            cursorBrush = SolidColor(Theme.colors.primary)
        ) { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = Theme.typography.body,
                        color = Theme.colors.onSurfaceVariant
                    )
                }
                innerTextField()
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Theme.colors.onSurface.copy(alpha = 0.5f))
        )
    }
}

@Preview(name = "Light Mode")
@Composable
fun QRBuddyTextFieldLightPreview() {
    QrCodeBuddyTheme {
        Surface(color = Theme.colors.background) {
            QRBuddyTextField(
                value = "",
                onValueChange = {},
                placeholder = "Type something...",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Dark Mode")
@Composable
fun QRBuddyTextFieldDarkPreview() {
    QrCodeBuddyTheme(useDarkTheme = true) {
        Surface(color = Theme.colors.background) {
            QRBuddyTextField(
                value = "",
                onValueChange = {},
                placeholder = "Type something...",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "With Value")
@Composable
fun QRBuddyTextFieldWithValuePreview() {
    QrCodeBuddyTheme {
        Surface(color = Theme.colors.background) {
            QRBuddyTextField(
                value = "Hello, QRBuddy!",
                onValueChange = {},
                placeholder = "Type something...",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
