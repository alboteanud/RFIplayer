package com.craiovadata.rfiplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RFIRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9D0509),
    onPrimaryContainer = Color.White,
    secondary = RFIAccent,
    secondaryContainer = ButtonContainerDark,
    onSecondaryContainer = ButtonTextDark,
    tertiary = RFIRedDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFCAC7C7),
)

private val LightColorScheme = lightColorScheme(
    primary = RFIRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB00000),
    onPrimaryContainer = Color.White,
    secondary = RFIAccent,
    secondaryContainer = ButtonContainerLight,
    onSecondaryContainer = ButtonTextLight,
    tertiary = RFIRedDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF626262),
    onSurfaceVariant = Color(0xFFE8E8E8),
)

@Composable
fun RFIplayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content,
    )
}
