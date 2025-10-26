package com.iscoding.qrcode.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.iscoding.qrcode.theme.Theme

@Composable
fun QRBuddyIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Theme.colors.background
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

@Preview
@Composable
fun QRBuddyIconPreview() {
    QRBuddyIcon(
        imageVector = Theme.icons.Add,
        contentDescription = "Add"
    )
}