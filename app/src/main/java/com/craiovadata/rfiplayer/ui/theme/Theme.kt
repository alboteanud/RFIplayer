package com.craiovadata.rfiplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = RFIWhite,
    onPrimary = RFIBlack,
    primaryContainer = RFIWhite,
    onPrimaryContainer = RFIBlack,
    secondary = RFIWhite,
    secondaryContainer = RFISoftBlack,
    onSecondaryContainer = RFIWhite,
    tertiary = RFIWhite,
    background = RFIBlack,
    surface = RFIBlack,
    onBackground = RFIWhite,
    onSurface = RFIWhite,
    surfaceVariant = RFISoftBlack,
    onSurfaceVariant = RFIWhite,
    error = RFIWhite,
    onError = RFIBlack,
)

@Composable
fun RFIplayerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content,
    )
}
