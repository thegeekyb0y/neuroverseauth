package com.neuroverse.auth.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.neuroverse.auth.R
import com.neuroverse.auth.ui.components.AuthTextField
import com.neuroverse.auth.ui.components.ErrorBanner
import com.neuroverse.auth.ui.components.LoadingButton
import com.neuroverse.auth.ui.components.PasswordField
import com.neuroverse.auth.ui.formErrorText
import com.neuroverse.auth.ui.fieldErrorText
import com.neuroverse.auth.ui.theme.Dimens
import com.neuroverse.auth.viewmodel.AuthField
import com.neuroverse.auth.viewmodel.RegisterUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onSubmit: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameBlur: () -> Unit,
    onEmailBlur: () -> Unit,
    onPasswordBlur: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onLogIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val networkMessage = stringResource(R.string.error_network)
    val retryLabel = stringResource(R.string.action_retry)

    val nameFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }

    // Runs only when a submit failed, so blur validation never steals focus.
    LaunchedEffect(state.focusRequestId) {
        if (state.focusRequestId == 0) return@LaunchedEffect
        when (state.focusTarget) {
            AuthField.Name -> nameFocus.requestFocus()
            AuthField.Email -> emailFocus.requestFocus()
            AuthField.Password -> passwordFocus.requestFocus()
            null -> Unit
        }
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.action_back),
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
                // Keep fields above the keyboard: the window is adjustResize and
                // the app draws edge to edge.
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenGutter),
        ) {
            Spacer(Modifier.height(Dimens.Space4))

            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(Dimens.Space2))

            Text(
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(Dimens.SectionGap))

            AuthTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.field_name),
                error = fieldErrorText(state.nameError),
                enabled = !state.isSubmitting,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                onImeAction = { emailFocus.requestFocus() },
                autofillType = ContentType.PersonFullName,
                focusRequester = nameFocus,
                onBlur = onNameBlur,
            )

            Spacer(Modifier.height(Dimens.FieldGap))

            AuthTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.field_email),
                error = fieldErrorText(state.emailError),
                enabled = !state.isSubmitting,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onImeAction = { passwordFocus.requestFocus() },
                // NewUsername marks this as a credential to be saved by Autofill.
                autofillType = ContentType.EmailAddress + ContentType.NewUsername,
                focusRequester = emailFocus,
                onBlur = onEmailBlur,
            )

            Spacer(Modifier.height(Dimens.FieldGap))

            PasswordField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.field_password),
                error = fieldErrorText(state.passwordError),
                enabled = !state.isSubmitting,
                imeAction = ImeAction.Done,
                onImeAction = onSubmit,
                autofillType = ContentType.NewPassword,
                focusRequester = passwordFocus,
                onBlur = onPasswordBlur,
            )

            Spacer(Modifier.height(Dimens.FieldGap))

            ErrorBanner(message = formErrorText(state.formError))

            Spacer(Modifier.height(Dimens.FieldGap))

            LoadingButton(
                text = stringResource(R.string.action_create_account),
                loading = state.isSubmitting,
                onClick = onSubmit,
            )

            Spacer(Modifier.height(Dimens.Space2))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.register_footer_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(
                    onClick = onLogIn,
                    // Secondary, never primary: mustard text on beige fails contrast.
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.action_log_in),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            Spacer(Modifier.height(Dimens.Space6))
        }
    }
}
