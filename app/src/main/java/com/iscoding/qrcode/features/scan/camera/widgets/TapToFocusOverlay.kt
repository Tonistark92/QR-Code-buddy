package com.iscoding.qrcode.features.scan.camera.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun TapToFocusOverlay(tapPosition: Offset?) {
    if (tapPosition != null) {
        Box(
            modifier = Modifier
                .offset { IntOffset(tapPosition.x.toInt() - 100, tapPosition.y.toInt() - 100) }
                .size(70.dp)
                .border(2.dp, Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), shape = CircleShape)
            )
        }
    }
}