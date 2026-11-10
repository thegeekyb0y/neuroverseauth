package com.neuroverse.auth.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Decorative brand mark. Mustard fill with dark brown letter; it carries no
 * information, so it is hidden from TalkBack.
 */
@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "N",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.clearAndSetSemantics { },
            )
        }
    }
}
