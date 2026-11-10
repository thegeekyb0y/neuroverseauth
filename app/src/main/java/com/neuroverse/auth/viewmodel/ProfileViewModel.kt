package com.neuroverse.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.neuroverse.auth.data.AuthUser
import com.neuroverse.auth.domain.UserDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    /** Display name, or the email's local part when the profile has none. */
    val name: String? = null,
    val email: String = "",
    val initials: String = "",
)

/**
 * Read-only view of the signed-in user.
 *
 * The user comes from the session state handed down by the gate rather than a
 * second Firebase listener, so Profile cannot disagree with Home about who is
 * signed in.
 */
class ProfileViewModel(user: AuthUser) : ViewModel() {

    private val _state = MutableStateFlow(
        ProfileUiState(
            name = UserDisplay.greetingName(user.displayName, user.email),
            email = user.email.orEmpty(),
            initials = UserDisplay.initials(user.displayName, user.email),
        )
    )
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    companion object {
        fun factory(user: AuthUser): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ProfileViewModel(user) as T
            }
    }
}
