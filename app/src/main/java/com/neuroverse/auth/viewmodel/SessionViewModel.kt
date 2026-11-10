package com.neuroverse.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.neuroverse.auth.data.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    /**
     * True once this process has held a session.
     *
     * Plain field rather than saved state: it survives rotation with the view
     * model, and it resets when the process dies, which is exactly the rule we
     * want. A cold start lands on Welcome; a logout lands on Login.
     */
    private var hasSignedIn = false

    val state: StateFlow<SessionState> = repository.session
        .map { user ->
            if (user == null) {
                SessionState.SignedOut(
                    entry = if (hasSignedIn) AuthEntry.Login else AuthEntry.Welcome
                )
            } else {
                hasSignedIn = true
                SessionState.SignedIn(user)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionState.Loading,
        )

    fun signOut() {
        viewModelScope.launch { repository.signOut() }
    }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    SessionViewModel(repository) as T
            }
    }
}
