package com.neuroverse.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.neuroverse.auth.R
import com.neuroverse.auth.data.AuthError
import com.neuroverse.auth.domain.FieldError

/**
 * Mapping from domain enums to user-facing copy. Keeping it here means the view
 * models never touch string resources and the composables never invent text.
 */
@Composable
fun fieldErrorText(error: FieldError?): String? = when (error) {
    null -> null
    FieldError.NameBlank -> stringResource(R.string.error_name_blank)
    FieldError.EmailInvalid -> stringResource(R.string.error_email_invalid)
    FieldError.EmailInUse -> stringResource(R.string.error_email_in_use)
    FieldError.PasswordTooShort -> stringResource(R.string.error_password_short)
    FieldError.PasswordBlank -> stringResource(R.string.error_password_blank)
}

@Composable
fun formErrorText(error: AuthError?): String? = when (error) {
    null -> null
    AuthError.InvalidCredentials -> stringResource(R.string.error_invalid_credentials)
    AuthError.EmailAlreadyInUse -> stringResource(R.string.error_email_in_use)
    AuthError.WeakPassword -> stringResource(R.string.error_password_short)
    AuthError.InvalidEmail -> stringResource(R.string.error_email_invalid)
    AuthError.Network -> stringResource(R.string.error_network)
    AuthError.TooManyRequests -> stringResource(R.string.error_too_many_requests)
    AuthError.Unknown -> stringResource(R.string.error_unknown)
}
