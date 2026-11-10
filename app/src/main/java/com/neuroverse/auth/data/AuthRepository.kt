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

    suspend fun signOut()
}
