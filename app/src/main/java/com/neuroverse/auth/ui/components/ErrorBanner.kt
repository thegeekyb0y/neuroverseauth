package com.neuroverse.auth.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.neuroverse.auth.R
import com.neuroverse.auth.ui.theme.Dimens
import com.neuroverse.auth.ui.theme.Motion

/**
 * Form-level error message. Pass null to hide it.
 *
 * Marked as a polite live region so TalkBack announces the message when it
 * appears, without interrupting whatever the user is reading.
 */
@Composable
fun ErrorBanner(
    message: String?,
    modifier: Modifier = Modifier,
) {
    // Keeps the text on screen while the collapse animation plays out, instead of
    // animating to an empty pill.
    var lastMessage by remember { mutableStateOf("") }
    SideEffect { if (message != null) lastMessage = message }

    AnimatedVisibility(
        visible = message != null,
        modifier = modifier,
        enter = expandVertically(animationSpec = tween(Motion.Medium)) +
            fadeIn(animationSpec = tween(Motion.Medium)),
        exit = shrinkVertically(animationSpec = tween(Motion.Medium)) +
            fadeOut(animationSpec = tween(Motion.Medium)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(horizontal = Dimens.Space3, vertical = Dimens.Space3)
                .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space2),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_error),
                // Decorative: the text beside it carries the meaning.
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}
