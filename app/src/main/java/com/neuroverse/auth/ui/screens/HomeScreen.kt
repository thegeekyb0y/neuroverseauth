package com.neuroverse.auth.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.neuroverse.auth.R
import com.neuroverse.auth.ui.theme.Dimens

// TODO(Milestone 3): replace this placeholder with the real Home screen.
@Composable
fun HomeScreen(
    email: String,
    showWelcome: Boolean,
    onWelcomeShown: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val welcomeMessage = stringResource(R.string.home_welcome)

    // Fires once per Home composition. Keying on Unit (not on the flag) matters:
    // consuming the flag would otherwise restart this effect and cancel the
    // snackbar before it could show.
    LaunchedEffect(Unit) {
        if (!showWelcome) return@LaunchedEffect
        onWelcomeShown()
        snackbarHostState.showSnackbar(
            message = welcomeMessage,
            duration = SnackbarDuration.Long,
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenGutter),
        ) {
            Spacer(Modifier.height(Dimens.Space12))

            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(Dimens.Space2))

            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(Dimens.SectionGap))

            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight),
                shape = MaterialTheme.shapes.large,
            ) {
                Text(
                    text = stringResource(R.string.action_sign_out),
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(Modifier.height(Dimens.Space6))
        }
    }
}
