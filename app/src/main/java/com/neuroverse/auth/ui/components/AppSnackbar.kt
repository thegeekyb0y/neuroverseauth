package com.neuroverse.auth.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * One snackbar look for the app.
 *
 * Material's default snackbar is the inverse surface, whose action colour is
 * `inversePrimary`. In the dark palette that is mustard on beige at 2.01:1, so
 * the colours are pinned to roles that hold in both themes instead.
 */
@Composable
fun AppSnackbar(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        Snackbar(
            snackbarData = data,
            shape = MaterialTheme.shapes.medium,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface,
            actionContentColor = MaterialTheme.colorScheme.secondary,
            dismissActionContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
