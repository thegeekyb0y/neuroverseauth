package com.neuroverse.auth.viewmodel

import com.neuroverse.auth.data.AuthUser

/**
 * Root navigation state. SignedIn is only produced once the repository has a
 * user whose profile is ready, so Home can trust displayName.
 */
sealed interface SessionState {
    data object Loading : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val user: AuthUser) : SessionState
}
