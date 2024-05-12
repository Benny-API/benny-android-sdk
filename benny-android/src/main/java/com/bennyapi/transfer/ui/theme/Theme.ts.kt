package com.bennyapi.transfer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily.Companion.SansSerif
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.sp

internal val Black = Color(0xFF000000)
internal val Brand = Color(0xFF131DAA)
internal val BrandLight = Color(0xFF6770EF)
internal val GreyDark = Color(0xFF6C6C71)
internal val GreyLight = Color(0xFFEEEEEE)
internal val Red = Color(0xFFDA0069)
internal val White = Color(0xFFFFFFFF)

internal val Typography = Typography(
    titleSmall = TextStyle(
        fontFamily = SansSerif,
        fontWeight = SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = Black,
    ),
    bodyLarge = TextStyle(
        fontFamily = SansSerif,
        fontWeight = SemiBold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = SansSerif,
        fontWeight = Normal,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        color = GreyDark,
    ),
    labelLarge = TextStyle(
        fontFamily = SansSerif,
        fontWeight = SemiBold,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        color = GreyDark,
    ),
)

private val ColorScheme = lightColorScheme(
    primary = Brand,
    secondary = BrandLight,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Black,
    onSurface = Black,
)

@Composable
internal fun BennySdkTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content,
    )
}
