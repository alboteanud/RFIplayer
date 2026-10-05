package com.craiovadata.rfiplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AppWhite,
    onPrimary = AppBlack,
    primaryContainer = AppWhite,
    onPrimaryContainer = AppBlack,
    secondary = AppWhite,
    secondaryContainer = AppSoftBlack,
    onSecondaryContainer = AppWhite,
    tertiary = AppWhite,
    background = AppBlack,
    surface = AppBlack,
    onBackground = AppWhite,
    onSurface = AppWhite,
    surfaceVariant = AppSoftBlack,
    onSurfaceVariant = AppWhite,
    error = AppWhite,
    onError = AppBlack,
)

@Composable
fun RadioPlayerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content,
    )
}
