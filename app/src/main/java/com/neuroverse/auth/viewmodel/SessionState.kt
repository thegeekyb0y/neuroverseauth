package com.neuroverse.auth.viewmodel

import com.neuroverse.auth.data.AuthUser

/** Which screen the signed-out auth graph should open on. */
enum class AuthEntry {
    /** Cold start with no session: the product's Welcome screen. */
    Welcome,

    /** The user just signed out (or their token was revoked): go straight to Login. */
    Login,
}

/**
 * Root navigation state. SignedIn is only produced once the repository has a
 * user whose profile is ready, so Home can trust displayName.
 */
sealed interface SessionState {
    data object Loading : SessionState

    data class SignedOut(val entry: AuthEntry = AuthEntry.Welcome) : SessionState

    data class SignedIn(val user: AuthUser) : SessionState
}
