package com.neuroverse.auth.viewmodel

import com.google.firebase.auth.FirebaseAuthException
import com.neuroverse.auth.MainDispatcherRule
import com.neuroverse.auth.data.AuthError
import com.neuroverse.auth.domain.FieldError
import com.neuroverse.auth.fakes.FakeAuthRepository
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAuthRepository()

    private fun viewModelWith(email: String = "ada@example.com", password: String = "secret123") =
        LoginViewModel(repository).apply {
            onEmailChange(email)
            onPasswordChange(password)
        }

    @Test
    fun `blank fields are rejected before any network call`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = LoginViewModel(repository)

        viewModel.submit()

        assertEquals(FieldError.EmailInvalid, viewModel.state.value.emailError)
        assertEquals(FieldError.PasswordBlank, viewModel.state.value.passwordError)
        assertEquals(AuthField.Email, viewModel.state.value.focusTarget)
        assertEquals(1, viewModel.state.value.focusRequestId)
        assertEquals(0, repository.signInCalls)
    }

    @Test
    fun `wrong password shows one generic banner and blames no field`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.signInFailure = FirebaseAuthException("ERROR_INVALID_CREDENTIAL", "raw server text")
            val viewModel = viewModelWith()

            viewModel.submit()

            val state = viewModel.state.value
            assertEquals(AuthError.InvalidCredentials, state.formError)
            assertNull(state.emailError)
            assertNull(state.passwordError)
            assertFalse(state.isSubmitting)
        }

    @Test
    fun `unknown account is indistinguishable from a wrong password`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.signInFailure = FirebaseAuthException("ERROR_USER_NOT_FOUND", "raw server text")
            val viewModel = viewModelWith()

            viewModel.submit()

            assertEquals(AuthError.InvalidCredentials, viewModel.state.value.formError)
        }

    @Test
    fun `network failure raises the retry snackbar instead of a banner`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.signInFailure = IOException("offline")
            val viewModel = viewModelWith()

            viewModel.submit()

            val state = viewModel.state.value
            assertEquals(1, state.networkErrorId)
            assertNull(state.formError)
            assertNull(state.emailError)
        }

    @Test
    fun `loading flag is set while the call is in flight`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val gate = CompletableDeferred<Unit>()
            repository.gate = gate
            // A failing call, so the flag has to clear once the request returns.
            repository.signInFailure = IllegalStateException("boom")
            val viewModel = viewModelWith()

            viewModel.submit()
            assertTrue(viewModel.state.value.isSubmitting)

            gate.complete(Unit)
            runCurrent()
            assertFalse(viewModel.state.value.isSubmitting)
        }

    @Test
    fun `success leaves the spinner up until the gate swaps screens`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = viewModelWith()

            viewModel.submit()

            assertTrue(viewModel.state.value.isSubmitting)
            assertNull(viewModel.state.value.formError)
        }

    @Test
    fun `editing a field clears its own error`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = LoginViewModel(repository)
        viewModel.submit()

        viewModel.onEmailChange("ada@example.com")

        assertNull(viewModel.state.value.emailError)
    }

    @Test
    fun `email is trimmed before it reaches the repository`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = viewModelWith(email = "  ada@example.com  ")

            viewModel.submit()

            assertEquals(1, repository.signInCalls)
            assertEquals("ada@example.com", repository.lastSignInEmail)
        }
}
