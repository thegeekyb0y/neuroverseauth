package com.neuroverse.auth.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.neuroverse.auth.R
import com.neuroverse.auth.ui.theme.Dimens
import com.neuroverse.auth.ui.theme.Motion

/**
 * Confirmation before signing out.
 *
 * Built on a plain Dialog rather than AlertDialog so the panel can scale from
 * 0.92 to 1 while fading in, 200 ms. AlertDialog reports a fixed size to the
 * platform and would clip a scaling child.
 */
@Composable
fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        var appeared by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { appeared = true }

        val progress by animateFloatAsState(
            targetValue = if (appeared) 1f else 0f,
            animationSpec = tween(durationMillis = Motion.Medium, easing = Motion.Standard),
            label = "logoutDialog",
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val scale = 0.92f + (0.08f * progress)
                    scaleX = scale
                    scaleY = scale
                    alpha = progress
                },
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(Dimens.Space6)) {
                Text(
                    text = stringResource(R.string.logout_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(Dimens.Space3))

                Text(
                    text = stringResource(R.string.logout_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(Dimens.Space5))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.action_cancel),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    TextButton(
                        onClick = onConfirm,
                        colors = ButtonDefaults.textButtonColors(
                            // 5.07:1 light, 8.7:1 dark against surfaceContainerHigh.
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.action_log_out),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}
