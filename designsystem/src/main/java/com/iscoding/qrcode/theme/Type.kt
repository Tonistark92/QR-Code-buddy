package com.iscoding.qrcode.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun headlineLarge(): TextStyle {
    return TextStyle(
        fontSize = 40.sp,
        lineHeight = 83.sp,
//        fontFamily = FontFamily(Font(R.font.baloo2_bold)),
        fontWeight = FontWeight.W400,
        letterSpacing = 9.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun headline(): TextStyle {
    return TextStyle(
        fontSize = 22.sp,
        lineHeight = 34.5.sp,
//        fontFamily = FontFamily(Font(R.font.baloo2_bold)),
        fontWeight = FontWeight.W400,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun titleLarge(): TextStyle {
    return TextStyle(
        fontSize = 18.sp,
        lineHeight = 20.sp,
//        fontFamily = FontFamily(Font(R.font.baloo2_medium)),
        fontWeight = FontWeight.W400,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun title(): TextStyle {
    return TextStyle(
        fontSize = 16.sp,
//        fontFamily = FontFamily(Font(R.font.baloo2_medium)),
        fontWeight = FontWeight.W400,
        lineHeight = 22.04.sp,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun titleMedium(): TextStyle {
    return TextStyle(
        fontSize = 14.sp,
//        fontFamily = FontFamily(Font(R.font.baloo2_medium)),
        fontWeight = FontWeight.W400,
        lineHeight = 22.04.sp,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun titleSmall(): TextStyle {
    return TextStyle(
        fontSize = 12.sp,
//        fontFamily = FontFamily(Font(R.font.baloo2_bold)),
        fontWeight = FontWeight.W600,
        lineHeight = 19.22.sp,
    )
}

@Composable
fun body(): TextStyle {
    return TextStyle(
        fontSize = 14.sp,
        lineHeight = 22.43.sp,
//        fontFamily = FontFamily(Font(R.font.baloo2_medium)),
        fontWeight = FontWeight.W400,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun caption(): TextStyle {
    return TextStyle(
//        fontFamily = FontFamily(Font(R.font.baloo2_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 14.5.sp,
    )
}

//@Composable
//fun placeHolder(): TextStyle {
//
//    return TextStyle(
//        fontWeight = FontWeight.W600,
//        fontSize = 12.sp,
//        lineHeight = 19.22.sp,
//        color = Theme.colors.gray600,
//        spanStyle = SpanStyle(),
//        paragraphStyle = TODO()
//    )
//}
//val Typography =
//    Typography(
//        bodyLarge =
//            TextStyle(
//                fontFamily = FontFamily.Default,
//                fontWeight = FontWeight.Normal,
//                fontSize = 16.sp,
//                lineHeight = 24.sp,
//                letterSpacing = 0.5.sp,
//            ),
        /* Other default text styles to override
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
         */
//    )

@Composable
fun badge() : TextStyle{
    return TextStyle(
//        fontFamily = FontFamily(Font(R.font.baloo2_bold)),
        fontSize = 11.sp,
        lineHeight = 17.62.sp,
        fontWeight = FontWeight.W800
    )
}

@Composable
fun cardTitle() : TextStyle {
    return TextStyle(
//        fontFamily = FontFamily(Font(R.font.baloo2_regular)),
        fontSize = 10.sp,
        lineHeight = 16.02.sp,
        fontWeight = FontWeight.W600
    )
}

@Composable
fun cardSubtitle() : TextStyle {
    return TextStyle(
//        fontFamily = FontFamily(Font(R.font.baloo2_regular)),
        fontSize = 7.sp,
        lineHeight = 11.21.sp,
        fontWeight = FontWeight.W600
    )
}