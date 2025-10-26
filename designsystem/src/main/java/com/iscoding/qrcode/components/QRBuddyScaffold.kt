package com.iscoding.qrcode.components

import android.R.attr.theme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iscoding.qrcode.theme.Theme

@Composable
fun QRBuddyScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = floatingActionButton,
        topBar = topBar,
        containerColor = Theme.colors.background
    ) { paddingValues ->
        content(paddingValues)
    }
}
