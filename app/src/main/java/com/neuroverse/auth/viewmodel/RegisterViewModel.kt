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

/** Which field the screen should focus after a failed submit. */
enum class AuthField { Name, Email, Password }

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: FieldError? = null,
    val emailError: FieldError? = null,
    val passwordError: FieldError? = null,
    val formError: AuthError? = null,
    val isSubmitting: Boolean = false,
    val focusTarget: AuthField? = null,
    /** Bumped on every failed submit so the screen can re-run the focus effect. */
    val focusRequestId: Int = 0,
    /** Bumped on every network failure, drives the retry snackbar. */
    val networkErrorId: Int = 0,
)

class RegisterViewModel(
    private val repository: AuthRepository,
    private val welcomeSignal: WelcomeSignal,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNameChange(value: String) {
        _state.update { it.copy(name = value, nameError = null, formError = null) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = null, formError = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, passwordError = null, formError = null) }
    }

    // Validation on blur: the user has finished with the field, so a complaint is
    // fair. Never on every keystroke, which would scold mid-typing.
    fun onNameBlur() {
        _state.update { it.copy(nameError = Validators.validateName(it.name)) }
    }

    fun onEmailBlur() {
        _state.update { it.copy(emailError = Validators.validateEmail(it.email)) }
    }

    fun onPasswordBlur() {
        _state.update { it.copy(passwordError = Validators.validateNewPassword(it.password)) }
    }

    fun onRetry() = submit()

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        val nameError = Validators.validateName(current.name)
        val emailError = Validators.validateEmail(current.email)
        val passwordError = Validators.validateNewPassword(current.password)
        val firstInvalid = when {
            nameError != null -> AuthField.Name
            emailError != null -> AuthField.Email
            passwordError != null -> AuthField.Password
            else -> null
        }

        _state.update {
            it.copy(
                nameError = nameError,
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
                repository.register(
                    displayName = current.name.trim(),
                    email = current.email.trim(),
                    password = current.password,
                )
            }.onSuccess {
                // No navigation here on purpose. The session gate owns routing;
                // marking the flag is all Home needs. isSubmitting stays true so
                // the button does not flicker back before the screen swaps.
                welcomeSignal.markPending()
            }.onFailure { throwable ->
                applyFailure(throwable.toAuthError())
            }
        }
    }

    private fun applyFailure(error: AuthError) {
        when {
            error == AuthError.Network -> _state.update {
                it.copy(isSubmitting = false, networkErrorId = it.networkErrorId + 1)
            }

            error == AuthError.EmailAlreadyInUse -> _state.update {
                it.copy(
                    isSubmitting = false,
                    emailError = FieldError.EmailInUse,
                    focusTarget = AuthField.Email,
                    focusRequestId = it.focusRequestId + 1,
                )
            }

            error == AuthError.WeakPassword -> _state.update {
                it.copy(
                    isSubmitting = false,
                    passwordError = FieldError.PasswordTooShort,
                    focusTarget = AuthField.Password,
                    focusRequestId = it.focusRequestId + 1,
                )
            }

            error == AuthError.InvalidEmail -> _state.update {
                it.copy(
                    isSubmitting = false,
                    emailError = FieldError.EmailInvalid,
                    focusTarget = AuthField.Email,
                    focusRequestId = it.focusRequestId + 1,
                )
            }

            // No field to blame: rate limits and unknown failures get a banner.
            else -> _state.update { it.copy(isSubmitting = false, formError = error) }
        }
    }

    companion object {
        fun factory(
            repository: AuthRepository,
            welcomeSignal: WelcomeSignal,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                RegisterViewModel(repository, welcomeSignal) as T
        }
    }
}
