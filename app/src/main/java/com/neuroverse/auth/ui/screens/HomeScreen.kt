package com.neuroverse.auth.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.neuroverse.auth.R
import com.neuroverse.auth.data.AuthUser
import com.neuroverse.auth.domain.UserDisplay
import com.neuroverse.auth.ui.components.AppSnackbar
import com.neuroverse.auth.ui.components.AuthTopBar
import com.neuroverse.auth.ui.components.LogoutDialog
import com.neuroverse.auth.ui.theme.Dimens

@Composable
fun HomeScreen(
    user: AuthUser,
    showWelcome: Boolean,
    onWelcomeShown: () -> Unit,
    onSignOut: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val welcomeMessage = stringResource(R.string.home_welcome)
    var showLogoutDialog by remember { mutableStateOf(false) }

    // The gate can swap to Home a moment before the registration flag lands, so
    // the flag is watched rather than sampled once. Consuming it immediately and
    // showing from a separate effect means neither ordering loses the snackbar,
    // and a rotation cannot replay it.
    var shouldShowWelcome by remember { mutableStateOf(false) }

    LaunchedEffect(showWelcome) {
        if (showWelcome) {
            onWelcomeShown()
            shouldShowWelcome = true
        }
    }

    LaunchedEffect(shouldShowWelcome) {
        if (!shouldShowWelcome) return@LaunchedEffect
        shouldShowWelcome = false
        snackbarHostState.showSnackbar(
            message = welcomeMessage,
            duration = SnackbarDuration.Long,
        )
    }

    val greetingName = UserDisplay.greetingName(user.displayName, user.email)
        ?: stringResource(R.string.home_greeting_fallback)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { AppSnackbar(snackbarHostState) },
        topBar = {
            AuthTopBar(
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = stringResource(R.string.action_log_out),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenGutter),
        ) {
            Spacer(Modifier.height(Dimens.Space4))

            Text(
                text = stringResource(R.string.home_greeting, greetingName),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(Dimens.SectionGap))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            ) {
                Column(modifier = Modifier.padding(Dimens.Space5)) {
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.height(Dimens.Space1))

                    Text(
                        text = user.email.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(Dimens.Space4))

            Card(
                onClick = onOpenProfile,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            ) {
                Column(modifier = Modifier.padding(Dimens.Space5)) {
                    Text(
                        text = stringResource(R.string.home_profile_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(Modifier.height(Dimens.Space1))

                    Text(
                        text = stringResource(R.string.home_profile_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(Dimens.Space6))
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                onSignOut()
            },
            onDismiss = { showLogoutDialog = false },
        )
    }
}
