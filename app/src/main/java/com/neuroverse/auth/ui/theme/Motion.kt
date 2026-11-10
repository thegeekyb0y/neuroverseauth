package com.neuroverse.auth.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

/**
 * Material motion tokens. Nothing here exceeds 400ms, and no animation library
 * is used beyond Compose's own animation core.
 */
object Motion {
    const val Instant = 100
    const val Short = 150
    const val Medium = 200
    const val Screen = 300
    const val Long = 400

    val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
}
