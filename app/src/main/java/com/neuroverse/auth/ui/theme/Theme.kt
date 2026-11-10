package com.neuroverse.auth.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Fixed brand theme. Dynamic colour is intentionally disabled so the beige and
 * mustard palette is identical on every device, in light and dark.
 */
@Composable
fun NeuroVerseAuthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) NeuroDarkColorScheme else NeuroLightColorScheme,
        typography = NeuroTypography,
        shapes = NeuroShapes,
        content = content,
    )
}
