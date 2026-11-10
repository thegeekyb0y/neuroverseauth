package com.neuroverse.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neuroverse.auth.data.AuthError
import com.neuroverse.auth.data.AuthRepository
import com.neuroverse.auth.data.toAuthError
import com.neuroverse.auth.domain.FieldError
import com.neuroverse.auth.domain.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: FieldError? = null,
    val passwordError: FieldError? = null,
    val formError: AuthError? = null,
    val isSubmitting: Boolean = false,
    val focusTarget: AuthField? = null,
    val focusRequestId: Int = 0,
    val networkErrorId: Int = 0,
)

class LoginViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = null, formError = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, passwordError = null, formError = null) }
    }

    fun onEmailBlur() {
        _state.update { it.copy(emailError = Validators.validateEmail(it.email)) }
    }

    fun onPasswordBlur() {
        _state.update { it.copy(passwordError = Validators.validateExistingPassword(it.password)) }
    }

    fun onRetry() = submit()

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        val emailError = Validators.validateEmail(current.email)
        val passwordError = Validators.validateExistingPassword(current.password)
        val firstInvalid = when {
            emailError != null -> AuthField.Email
            passwordError != null -> AuthField.Password
            else -> null
        }

        _state.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                focusTarget = firstInvalid,
                focusRequestId = if (firstInvalid != null) it.focusRequestId + 1 else it.focusRequestId,
            )
        }

        if (firstInvalid != null) return

        _state.update { it.copy(isSubmitting = true, formError = null) }

        viewModelScope.launch {
            runCatching {
                repository.signIn(
                    email = current.email.trim(),
                    password = current.password,
                )
            }.onSuccess {
                // The gate routes to Home. Keep the spinner up so the button does
                // not blink back to idle during the swap.
            }.onFailure { throwable ->
                val error = throwable.toAuthError()
                _state.update {
                    it.copy(
                        isSubmitting = false,
                        // One generic banner. Never says which half was wrong,
                        // and never shows the raw server message.
                        formError = if (error == AuthError.Network) null else error,
                        networkErrorId = if (error == AuthError.Network) {
                            it.networkErrorId + 1
                        } else {
                            it.networkErrorId
                        },
                    )
                }
            }
        }
    }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    LoginViewModel(repository) as T
            }
    }
}
