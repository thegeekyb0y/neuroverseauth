package com.neuroverse.auth.data

import kotlinx.coroutines.flow.Flow

/**
 * Authentication boundary. Firebase is an implementation detail and never leaks
 * past this interface, so view models and composables stay testable.
 */
interface AuthRepository {

    /**
     * Emits the signed-in user, or null when signed out. The emission is gated
     * on the profile being ready, so a fresh registration never surfaces a blank
     * display name.
     */
    val session: Flow<AuthUser?>

    /**
     * Creates the account and writes the display name before returning.
     *
     * Declared as one call rather than "create" plus "set name" so a caller cannot
     * accidentally produce a signed-in user with a blank name. Failures surface as
     * Firebase or IO exceptions for the view model to map.
     */
    suspend fun register(displayName: String, email: String, password: String)

    suspend fun signIn(email: String, password: String)

    /**
     * Asks Firebase to email a reset link.
     *
     * The caller must treat the outcome as neutral: with email enumeration
     * protection on, an unknown address succeeds silently, and with it off the
     * call throws. Both paths have to look identical in the UI.
     */
    suspend fun sendPasswordReset(email: String)

    suspend fun signOut()
}
