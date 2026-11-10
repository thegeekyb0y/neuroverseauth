package com.neuroverse.auth.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthErrorTest {

    @Test
    fun `maps each supported firebase code`() {
        assertEquals(AuthError.InvalidEmail, authErrorForCode("ERROR_INVALID_EMAIL"))
        assertEquals(AuthError.WeakPassword, authErrorForCode("ERROR_WEAK_PASSWORD"))
        assertEquals(AuthError.EmailAlreadyInUse, authErrorForCode("ERROR_EMAIL_ALREADY_IN_USE"))
        assertEquals(AuthError.InvalidCredentials, authErrorForCode("ERROR_INVALID_CREDENTIAL"))
        assertEquals(AuthError.TooManyRequests, authErrorForCode("ERROR_TOO_MANY_REQUESTS"))
        assertEquals(AuthError.Network, authErrorForCode("ERROR_NETWORK_REQUEST_FAILED"))
    }

    @Test
    fun `wrong password and unknown account collapse to one category`() {
        // Keeping these distinct would turn the login form into an account-existence oracle.
        assertEquals(AuthError.InvalidCredentials, authErrorForCode("ERROR_WRONG_PASSWORD"))
        assertEquals(AuthError.InvalidCredentials, authErrorForCode("ERROR_USER_NOT_FOUND"))
    }

    @Test
    fun `unrecognised code falls back to unknown`() {
        assertEquals(AuthError.Unknown, authErrorForCode("ERROR_SOMETHING_NEW"))
        assertEquals(AuthError.Unknown, authErrorForCode(""))
    }

    @Test
    fun `firebase auth exception is mapped through its code`() {
        val error = FirebaseAuthException("ERROR_INVALID_CREDENTIAL", "raw server text").toAuthError()
        assertEquals(AuthError.InvalidCredentials, error)
    }

    @Test
    fun `firebase network exception is a network error`() {
        assertEquals(AuthError.Network, FirebaseNetworkException("offline").toAuthError())
    }

    @Test
    fun `io failure is a network error`() {
        assertEquals(AuthError.Network, IOException("socket closed").toAuthError())
    }

    @Test
    fun `anything else is unknown`() {
        assertEquals(AuthError.Unknown, IllegalStateException("boom").toAuthError())
        assertTrue(IllegalStateException("boom").toAuthError() == AuthError.Unknown)
    }
}
