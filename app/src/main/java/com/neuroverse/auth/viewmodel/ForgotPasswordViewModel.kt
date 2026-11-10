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

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: FieldError? = null,
    val isSubmitting: Boolean = false,
    /** True once the request finished, regardless of whether the account exists. */
    val isEmailAccepted: Boolean = false,
    val formError: AuthError? = null,
    val networkErrorId: Int = 0,
)

class ForgotPasswordViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordUiState())
    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, emailError = null, formError = null) }
    }

    fun onEmailBlur() {
        _state.update { it.copy(emailError = Validators.validateEmail(it.email)) }
    }

    fun onRetry() = submit()

    fun submit() {
        val current = _state.value
        if (current.isSubmitting) return

        val emailError = Validators.validateEmail(current.email)
        if (emailError != null) {
            _state.update { it.copy(emailError = emailError) }
            return
        }

        _state.update { it.copy(isSubmitting = true, formError = null) }

        viewModelScope.launch {
            runCatching {
                repository.sendPasswordReset(current.email.trim())
            }.onSuccess {
                _state.update { it.copy(isSubmitting = false, isEmailAccepted = true) }
            }.onFailure { throwable ->
                when (val error = throwable.toAuthError()) {
                    AuthError.Network -> _state.update {
                        it.copy(isSubmitting = false, networkErrorId = it.networkErrorId + 1)
                    }

                    AuthError.TooManyRequests,
                    AuthError.Unknown -> _state.update {
                        it.copy(isSubmitting = false, formError = error)
                    }

                    // "No such user" and "malformed address" land here. Showing a
                    // different answer from the success case would tell the caller
                    // whether the account exists, so both paths look identical.
                    else -> _state.update {
                        it.copy(isSubmitting = false, isEmailAccepted = true)
                    }
                }
            }
        }
    }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ForgotPasswordViewModel(repository) as T
            }
    }
}
