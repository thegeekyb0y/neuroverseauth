package com.neuroverse.auth.viewmodel

import com.google.firebase.auth.FirebaseAuthException
import com.neuroverse.auth.MainDispatcherRule
import com.neuroverse.auth.data.AuthError
import com.neuroverse.auth.domain.FieldError
import com.neuroverse.auth.fakes.FakeAuthRepository
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ForgotPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAuthRepository()

    private fun viewModelWith(email: String = "ada@example.com") =
        ForgotPasswordViewModel(repository).apply { onEmailChange(email) }

    @Test
    fun `invalid email is rejected without a call`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModelWith(email = "ada@")

        viewModel.submit()

        assertEquals(FieldError.EmailInvalid, viewModel.state.value.emailError)
        assertEquals(0, repository.resetCalls)
    }

    @Test
    fun `success shows the neutral card`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModelWith()

        viewModel.submit()

        assertTrue(viewModel.state.value.isEmailAccepted)
        assertFalse(viewModel.state.value.isSubmitting)
        assertNull(viewModel.state.value.formError)
    }

    @Test
    fun `unknown account shows exactly the same neutral card`() =
        runTest(mainDispatcherRule.testDispatcher) {
            // Firebase throws this when enumeration protection is off. The UI must
            // not leak that the account is missing.
            repository.resetFailure = FirebaseAuthException("ERROR_USER_NOT_FOUND", "raw server text")
            val viewModel = viewModelWith()

            viewModel.submit()

            assertTrue(viewModel.state.value.isEmailAccepted)
            assertNull(viewModel.state.value.formError)
            assertNull(viewModel.state.value.emailError)
        }

    @Test
    fun `invalid credential error is also neutral`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.resetFailure = FirebaseAuthException("ERROR_INVALID_CREDENTIAL", "raw")
            val viewModel = viewModelWith()

            viewModel.submit()

            assertTrue(viewModel.state.value.isEmailAccepted)
        }

    @Test
    fun `network failure offers retry and does not claim success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.resetFailure = IOException("offline")
            val viewModel = viewModelWith()

            viewModel.submit()

            val state = viewModel.state.value
            assertEquals(1, state.networkErrorId)
            assertFalse(state.isEmailAccepted)
            assertNull(state.formError)
        }

    @Test
    fun `rate limiting gets a banner instead of a false success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.resetFailure = FirebaseAuthException("ERROR_TOO_MANY_REQUESTS", "raw")
            val viewModel = viewModelWith()

            viewModel.submit()

            assertEquals(AuthError.TooManyRequests, viewModel.state.value.formError)
            assertFalse(viewModel.state.value.isEmailAccepted)
        }

    @Test
    fun `loading flag is set while the request is in flight`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val gate = CompletableDeferred<Unit>()
            repository.gate = gate
            val viewModel = viewModelWith()

            viewModel.submit()
            assertTrue(viewModel.state.value.isSubmitting)

            gate.complete(Unit)
            assertFalse(viewModel.state.value.isSubmitting)
        }

    @Test
    fun `email is trimmed before the request`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModelWith(email = "  ada@example.com  ")

        viewModel.submit()

        assertEquals("ada@example.com", repository.lastResetEmail)
    }
}
