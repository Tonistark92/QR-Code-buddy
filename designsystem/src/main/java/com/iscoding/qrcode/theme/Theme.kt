package com.iscoding.qrcode.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val localColorScheme = staticCompositionLocalOf { LightColors }
private val localRadius = staticCompositionLocalOf { Radius() }
private val localTypography = staticCompositionLocalOf { Typography() }

//private val DarkColorScheme = darkColorScheme(
//    primary = Color(0xFF138173),
//    secondary = Color(0xFF8AE5D0),
//    tertiary = Color(0xFFDDE7E4),
//    background = Color.Black,
//    surface = Color.Black,
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Color(0xFF138173),
//    secondary = Color(0xFF8AE5D0),
//    tertiary = Color(0xFFDDE7E4),
//    background = Color.White,
//    surface = Color.White,
//)
@Composable
fun QrCodeBuddyTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
//    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
//    val colorScheme = when {
//        // If device supports dynamic color (Android 12+)
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (useDarkTheme) dynamicDarkColorScheme(context)
//            else dynamicLightColorScheme(context)
//        }
//
//        // Otherwise, use your custom fallback colors
//        useDarkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
    val colorScheme = if (useDarkTheme) DarkColor else LightColors


    val typography = Typography(
        headlineLarge = headlineLarge(),
        headline = headline(),
        titleLarge = titleLarge(),
        title = title(),
        titleMedium = titleMedium(),
        body = body(),
        caption = caption(),
        badge = badge(),
        cardTitle = cardTitle(),
        cardSubtitle = cardSubtitle(),
        titleSmall = titleSmall(),
    )
    CompositionLocalProvider(
        localColorScheme provides colorScheme,
        localTypography provides typography,
        localRadius provides Radius(),
        content = {content ()}
    )

}

object Theme {
    val colors: QrCodeColors
        @Composable
        @ReadOnlyComposable
        get() = localColorScheme.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = localTypography.current

    val radius: Radius
        @Composable
        @ReadOnlyComposable
        get() = localRadius.current

    val spacing: QrBuddySpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalQrBuddySpacing.current

    val icons: QrBuddyIcons
        @Composable
        @ReadOnlyComposable
        get() = LocalQrBuddyIcons.current

    val shapes: QRBuddyShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalQRBuddyShapes.current

}