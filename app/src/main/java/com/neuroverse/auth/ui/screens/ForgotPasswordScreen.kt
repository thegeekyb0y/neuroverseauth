package com.neuroverse.auth.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.neuroverse.auth.R
import com.neuroverse.auth.ui.components.AppSnackbar
import com.neuroverse.auth.ui.components.AuthTextField
import com.neuroverse.auth.ui.components.AuthTopBar
import com.neuroverse.auth.ui.components.ErrorBanner
import com.neuroverse.auth.ui.components.LoadingButton
import com.neuroverse.auth.ui.fieldErrorText
import com.neuroverse.auth.ui.formErrorText
import com.neuroverse.auth.ui.theme.Dimens
import com.neuroverse.auth.viewmodel.ForgotPasswordUiState

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordUiState,
    onSubmit: () -> Unit,
    onEmailChange: (String) -> Unit,
    onEmailBlur: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val networkMessage = stringResource(R.string.error_network)
    val retryLabel = stringResource(R.string.action_retry)

    LaunchedEffect(state.networkErrorId) {
        if (state.networkErrorId == 0) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = networkMessage,
            actionLabel = retryLabel,
            duration = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) onRetry()
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { AppSnackbar(snackbarHostState) },
        topBar = {
            AuthTopBar(
                title = stringResource(R.string.forgot_title),
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenGutter),
        ) {
            Spacer(Modifier.height(Dimens.Space4))

            if (state.isEmailAccepted) {
                // The neutral outcome. Same card for a real account and for an
                // address nobody has ever registered.
                ResetSentCard(onBackToLogin = onBack)
            } else {
                Text(
                    text = stringResource(R.string.forgot_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(Dimens.SectionGap))

                AuthTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.field_email),
                    error = fieldErrorText(state.emailError),
                    enabled = !state.isSubmitting,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                    onImeAction = onSubmit,
                    autofillType = ContentType.EmailAddress,
                    onBlur = onEmailBlur,
                )

                Spacer(Modifier.height(Dimens.FieldGap))

                ErrorBanner(message = formErrorText(state.formError))

                Spacer(Modifier.height(Dimens.FieldGap))

                LoadingButton(
                    text = stringResource(R.string.action_send_reset),
                    loading = state.isSubmitting,
                    onClick = onSubmit,
                )
            }

            Spacer(Modifier.height(Dimens.Space6))
        }
    }
}

@Composable
private fun ResetSentCard(onBackToLogin: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(modifier = Modifier.padding(Dimens.Space5)) {
            Text(
                text = stringResource(R.string.forgot_success),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(Dimens.Space4))

            LoadingButton(
                text = stringResource(R.string.action_back_to_login),
                loading = false,
                onClick = onBackToLogin,
            )
        }
    }
}
