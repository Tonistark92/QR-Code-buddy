package com.iscoding.qrcode.theme

import androidx.compose.ui.graphics.Color


//data class Colors(
//    val purple: Color,
//    val purple300: Color,
//    val purple200: Color,
//    val purple100: Color,
//    val black: Color,
//    val black300: Color,
//    val black200: Color,
//    val black100: Color,
//    val spindle200: Color,
//    val spindle400: Color,
//    val spindle100: Color,
//    val spindle300: Color,
//    val spindle500: Color,
//    val spindle600: Color,
//    val gray700: Color,
//    val gray600: Color,
//    val gray500: Color,
//    val gray400: Color,
//    val gray200: Color,
//    val gray300: Color,
//    val gray100: Color,
//    val gray90: Color,
//    val grayLight: Color,
//    val gray: Color,
//    val pink: Color,
//    val pink100: Color,
//    val pink200: Color,
//    val pink300: Color,
//    val pink400: Color,
//    val red: Color,
//    val lightRed: Color,
//    val red100: Color,
//    val red200: Color,
//    val red300: Color,
//    val red400: Color,
//    val red500: Color,
//    val yellow: Color,
//    val yellow100: Color,
//    val yellow200: Color,
//    val yellow300: Color,
//    val orange: Color,
//    val lightOrange: Color,
//    val blue: Color,
//    val lightBlue: Color,
//    val green: Color,
//    val green100: Color,
//    val green200: Color,
//    val lightGreen: Color,
//)

val Blue = Color(0xFF007AFF)
val LightGray = Color(0xFFE5E5EA)
val DarkGray = Color(0xFF1C1C1E)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

data class QrCodeColors(
    val primary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
)


val LightColors = QrCodeColors(
    primary = Blue,
    background = White,
    surface = LightGray,
    onPrimary = White,
    onBackground = Black,
    onSurface = Black,
    onSurfaceVariant = Black.copy(alpha = 0.5f)
)

val DarkColor = QrCodeColors(
    primary = Blue,
    background = Black,
    surface = DarkGray,
    onPrimary = White,
    onBackground = White,
    onSurface = White,
    onSurfaceVariant = White.copy(alpha = 0.5f)
)
