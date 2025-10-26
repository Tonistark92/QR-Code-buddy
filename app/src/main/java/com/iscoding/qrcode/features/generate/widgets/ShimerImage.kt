package com.iscoding.qrcode.features.generate.widgets

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * A composable that shows a shimmer placeholder while content is loading.
 *
 * When [isLoading] is true, a shimmer effect box is displayed as a placeholder.
 * When loading is finished, [contentAfterLoading] is rendered instead.
 *
 * @param isLoading Whether to show the shimmer effect (true) or the actual content (false).
 * @param modifier Modifier to be applied to the shimmer container.
 * @param contentAfterLoading The composable content to display once loading is finished.
 */
@Composable
fun ShimmerImage(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    contentAfterLoading: @Composable () -> Unit,
) {
    if (isLoading) {
        Row(modifier = modifier) {
            Box(
                modifier =
                Modifier
                    .size(200.dp)
                    .clip(RectangleShape)
                    .shimmerEffect(),
            )
        }
    } else {
        contentAfterLoading()
    }
}

/**
 * Adds a shimmering placeholder effect to any [Modifier].
 *
 * This uses an infinite linear gradient animation that sweeps horizontally
 * across the component to simulate a loading shimmer effect.
 *
 * @receiver Modifier to which the shimmer effect will be applied.
 * @return A [Modifier] with shimmer animation applied.
 */
fun Modifier.shimmerEffect(): Modifier =
    composed {
        var size by remember {
            mutableStateOf(IntSize.Zero)
        }
        val transition = rememberInfiniteTransition()
        val startOffsetX by transition.animateFloat(
            initialValue = -2 * size.width.toFloat(),
            targetValue = 2 * size.width.toFloat(),
            animationSpec =
            infiniteRepeatable(
                animation = tween(1000),
            ),
            label = "shimmer anim",
        )

        background(
            brush =
            Brush.linearGradient(
                colors =
                listOf(
                    Color(0xFFB8B5B5),
                    Color(0xFF8F8B8B),
                    Color(0xFFB8B5B5),
                ),
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()),
            ),
        )
            .onGloballyPositioned {
                size = it.size
            }
    }
