package com.iscoding.qrcode.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.iscoding.qrcode.theme.QrCodeBuddyTheme
import com.iscoding.qrcode.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRBuddyTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Theme.colors.surface,
            titleContentColor = Theme.colors.onSurface,
            actionIconContentColor = Theme.colors.onSurface,
        ),
        title = {
            CompositionLocalProvider(LocalTextStyle provides Theme.typography.title) {
                title()
            }
        },
        actions = actions
    )
}

@Preview(name = "Light Mode")
@Composable
fun QRBuddyTopAppBarLightPreview() {
    QrCodeBuddyTheme {
        QRBuddyTopAppBar(title = { Text("QRBuddy") })
    }
}

@Preview(name = "Dark Mode")
@Composable
fun QRBuddyTopAppBarDarkPreview() {
    QrCodeBuddyTheme(useDarkTheme = true) {
        QRBuddyTopAppBar(title = { Text("QRBuddy") })
    }
}

@Preview(name = "With Actions")
@Composable
fun QRBuddyTopAppBarWithActionsPreview() {
    QrCodeBuddyTheme {
        QRBuddyTopAppBar(
            title = { Text("QRBuddy") },
            actions = {
                QRBuddyButton(onClick = {}) {
                    Text("Action")
                }
            }
        )
    }
}
