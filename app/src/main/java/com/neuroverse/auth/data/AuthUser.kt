package com.neuroverse.auth.data

/**
 * The only user data the UI is allowed to see. Tokens are never part of it.
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
)
