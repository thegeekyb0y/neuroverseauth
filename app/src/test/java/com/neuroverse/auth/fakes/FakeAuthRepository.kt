package com.neuroverse.auth.fakes

import com.neuroverse.auth.data.AuthRepository
import com.neuroverse.auth.data.AuthUser
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Hand-written test double. It can fail on demand and hold a call open so tests
 * can observe the loading state, neither of which a real Firebase call allows.
 */
class FakeAuthRepository : AuthRepository {

    val sessionFlow = MutableStateFlow<AuthUser?>(null)
    override val session: Flow<AuthUser?> = sessionFlow

    /** When set, calls suspend here until the test completes it. */
    var gate: CompletableDeferred<Unit>? = null

    var registerFailure: Throwable? = null
    var signInFailure: Throwable? = null
    var resetFailure: Throwable? = null

    var registerCalls = 0
    var signInCalls = 0
    var resetCalls = 0
    var signOutCalls = 0

    var lastRegisterName: String? = null
    var lastRegisterEmail: String? = null
    var lastSignInEmail: String? = null
    var lastResetEmail: String? = null

    override suspend fun register(displayName: String, email: String, password: String) {
        registerCalls++
        lastRegisterName = displayName
        lastRegisterEmail = email
        gate?.await()
        registerFailure?.let { throw it }
        sessionFlow.value = AuthUser(uid = "uid-1", email = email, displayName = displayName)
    }

    override suspend fun signIn(email: String, password: String) {
        signInCalls++
        lastSignInEmail = email
        gate?.await()
        signInFailure?.let { throw it }
        sessionFlow.value = AuthUser(uid = "uid-1", email = email, displayName = null)
    }

    override suspend fun sendPasswordReset(email: String) {
        resetCalls++
        lastResetEmail = email
        gate?.await()
        resetFailure?.let { throw it }
    }

    override suspend fun signOut() {
        signOutCalls++
        sessionFlow.value = null
    }
}
