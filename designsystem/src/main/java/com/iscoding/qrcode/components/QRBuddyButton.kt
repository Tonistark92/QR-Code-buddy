package com.iscoding.qrcode.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.iscoding.qrcode.theme.QrCodeBuddyTheme
import com.iscoding.qrcode.theme.Theme

@Composable
fun QRBuddyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val backgroundColor =
        if (enabled) Theme.colors.primary else Theme.colors.primary.copy(alpha = 0.5f)
    val contentColor =
        if (enabled) Theme.colors.onPrimary else Theme.colors.onPrimary.copy(alpha = 0.5f)

    Surface(
        modifier = modifier
            .clip(Theme.shapes.medium)
            .clickable(enabled = enabled, onClick = onClick),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Theme.spacing.medium,
                vertical = Theme.spacing.small
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalTextStyle provides Theme.typography.button) {
                content()
            }
        }
    }
}

@Preview(name = "Light Mode")
@Composable
fun QRBuddyButtonLightPreview() {
    QrCodeBuddyTheme {
        Surface(color = Theme.colors.background) {
            QRBuddyButton(onClick = {}, modifier = Modifier.padding(Theme.spacing.medium)) {
                Text("QRBuddyButton")
            }
        }
    }
}

@Preview(name = "Dark Mode")
@Composable
fun QRBuddyButtonDarkPreview() {
    QrCodeBuddyTheme(useDarkTheme = true) {
        Surface(color = Theme.colors.background) {
            QRBuddyButton(onClick = {}, modifier = Modifier.padding(Theme.spacing.medium)) {
                Text("QRBuddyButton")
            }
        }
    }
}

@Preview(name = "Disabled")
@Composable
fun QRBuddyButtonDisabledPreview() {
    QrCodeBuddyTheme {
        Surface(color = Theme.colors.background) {
            QRBuddyButton(
                enabled = false,
                onClick = {},
                modifier = Modifier.padding(Theme.spacing.medium)
            ) {
                Text("QRBuddyButton")
            }
        }
    }
}
