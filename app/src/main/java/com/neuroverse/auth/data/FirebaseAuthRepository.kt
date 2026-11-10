package com.neuroverse.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) : AuthRepository {

    /**
     * Held while a profile write is in flight (registration updates the display
     * name right after account creation). The session collector waits on it, so
     * a half-written profile is never emitted as signed in.
     */
    private val profileGate = Mutex()

    override val session: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                trySend(null)
                return@AuthStateListener
            }
            launch {
                val ready = runCatching {
                    profileGate.withLock {
                        // Refresh so a display name written moments ago is
                        // visible before Home renders it.
                        user.reload().await()
                        auth.currentUser
                    }
                }.getOrNull()
                // On a network failure we keep the cached user: Firebase already
                // restored the session from disk and signing out would be wrong.
                trySend((ready ?: user).toAuthUser())
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    override suspend fun register(displayName: String, email: String, password: String) {
        // The whole sequence runs under the gate. createUser fires the auth state
        // listener immediately, and that listener blocks here until the display
        // name is written and reloaded, so SignedIn can never carry a null name.
        withProfileWrite {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = requireNotNull(result.user) {
                "Firebase created an account without a user"
            }
            val profile = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profile).await()
            user.reload().await()
        }
    }

    override suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    /**
     * Runs [block] while the session flow is paused. Registration uses this to
     * cover create-user plus updateProfile(displayName).
     */
    internal suspend fun <T> withProfileWrite(block: suspend () -> T): T =
        profileGate.withLock { block() }

    private fun FirebaseUser.toAuthUser(): AuthUser = AuthUser(
        uid = uid,
        email = email,
        displayName = displayName,
    )
}
