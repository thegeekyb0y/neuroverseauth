package com.neuroverse.auth.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/*
 * Beige and mustard palette.
 *
 * Mustard is a FILL colour only. It is never used for text, icons or borders on
 * beige: mustard #D9A62E on beige #FAF3E3 is 2.01:1. Text on mustard is always
 * dark brown (#2A1F0B), never white.
 */

// Light
private val LightPrimary = Color(0xFFD9A62E)
private val LightOnPrimary = Color(0xFF2A1F0B)
private val LightPrimaryContainer = Color(0xFFF6E3AE)
private val LightSecondary = Color(0xFF5A4A2E)
private val LightOnSecondary = Color(0xFFFFF9EF)
private val LightSecondaryContainer = Color(0xFFEDE0C3)
private val LightTertiary = Color(0xFF4E5A3A)
private val LightOnTertiary = Color(0xFFFFFFFF)
private val LightTertiaryContainer = Color(0xFFDCE7C4)
private val LightOnTertiaryContainer = Color(0xFF1A2210)
private val LightBackground = Color(0xFFFAF3E3)
private val LightOnBackground = Color(0xFF241B0E)
private val LightSurfaceVariant = Color(0xFFEBE0C9)
private val LightOnSurfaceVariant = Color(0xFF4C4231)
private val LightOutline = Color(0xFF7A6A4F)
private val LightOutlineVariant = Color(0xFFCDBFA4)
private val LightError = Color(0xFFB3261E)
private val LightOnError = Color(0xFFFFFFFF)
private val LightErrorContainer = Color(0xFFF9DEDC)
private val LightOnErrorContainer = Color(0xFF410E0B)
private val LightInverseSurface = Color(0xFF3A3020)
private val LightInverseOnSurface = Color(0xFFF7EFDD)

// Dark
private val DarkPrimary = Color(0xFFF2C24B)
private val DarkOnPrimary = Color(0xFF2A1F0B)
private val DarkPrimaryContainer = Color(0xFF6B4E12)
private val DarkOnPrimaryContainer = Color(0xFFFBE7B4)
private val DarkSecondary = Color(0xFFD8C6A4)
private val DarkOnSecondary = Color(0xFF2A2114)
private val DarkSecondaryContainer = Color(0xFF443722)
private val DarkOnSecondaryContainer = Color(0xFFF0E6D2)
private val DarkTertiary = Color(0xFFB9C79C)
private val DarkOnTertiary = Color(0xFF26301A)
private val DarkTertiaryContainer = Color(0xFF3C4A2A)
private val DarkOnTertiaryContainer = Color(0xFFD5E3BC)
private val DarkBackground = Color(0xFF171208)
private val DarkOnBackground = Color(0xFFF0E6D2)
private val DarkSurfaceVariant = Color(0xFF3A2F1E)
private val DarkOnSurfaceVariant = Color(0xFFD9CCB2)
private val DarkOutline = Color(0xFFA2906F)
private val DarkOutlineVariant = Color(0xFF4E4229)
private val DarkError = Color(0xFFFFB4AB)
private val DarkOnError = Color(0xFF690005)
private val DarkErrorContainer = Color(0xFF93000A)
private val DarkOnErrorContainer = Color(0xFFFFDAD6)
private val DarkInverseSurface = Color(0xFFF0E6D2)
private val DarkInverseOnSurface = Color(0xFF332A1A)

internal val NeuroLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimary,
    inversePrimary = DarkPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnPrimary,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightBackground,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceTint = LightPrimary,
    surfaceBright = Color(0xFFFAF3E3),
    surfaceDim = Color(0xFFE6DCC8),
    surfaceContainerLowest = Color(0xFFFFFCF5),
    surfaceContainerLow = Color(0xFFF5EEDD),
    surfaceContainer = Color(0xFFF0E8D6),
    surfaceContainerHigh = Color(0xFFEAE1CD),
    surfaceContainerHighest = Color(0xFFE4DAC4),
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = Color(0xFF000000),
)

internal val NeuroDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    inversePrimary = LightPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkBackground,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceTint = DarkPrimary,
    surfaceBright = Color(0xFF3E3420),
    surfaceDim = Color(0xFF171208),
    surfaceContainerLowest = Color(0xFF100C05),
    surfaceContainerLow = Color(0xFF1F190E),
    surfaceContainer = Color(0xFF231D11),
    surfaceContainerHigh = Color(0xFF2E2718),
    surfaceContainerHighest = Color(0xFF3A3120),
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = Color(0xFF000000),
)
