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
    primaryContainer = RFIRed,
    onPrimaryContainer = Color.White,
    secondary = RFIRed,
    secondaryContainer = ButtonContainerDark,
    onSecondaryContainer = ButtonTextDark,
    tertiary = RFIRed,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFE6E1E5),
)

private val LightColorScheme = lightColorScheme(
    primary = RFIRed,
    onPrimary = Color.White,
    primaryContainer = RFIRed,
    onPrimaryContainer = Color.White,
    secondary = RFIRed,
    secondaryContainer = ButtonContainerLight,
    onSecondaryContainer = ButtonTextLight,
    tertiary = RFIRed,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF212121),
)

@Composable
fun RFIplayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
