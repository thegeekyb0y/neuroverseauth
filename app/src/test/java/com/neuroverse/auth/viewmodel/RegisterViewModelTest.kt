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

class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAuthRepository()
    private val welcomeSignal = WelcomeSignal()

    private fun viewModelWith(
        name: String = "Ada Lovelace",
        email: String = "ada@example.com",
        password: String = "secret123",
    ) = RegisterViewModel(repository, welcomeSignal).apply {
        onNameChange(name)
        onEmailChange(email)
        onPasswordChange(password)
    }

    @Test
    fun `empty form reports three errors and focuses the name`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = RegisterViewModel(repository, welcomeSignal)

            viewModel.submit()

            val state = viewModel.state.value
            assertEquals(FieldError.NameBlank, state.nameError)
            assertEquals(FieldError.EmailInvalid, state.emailError)
            assertEquals(FieldError.PasswordTooShort, state.passwordError)
            assertEquals(AuthField.Name, state.focusTarget)
            assertEquals(1, state.focusRequestId)
            assertEquals(0, repository.registerCalls)
        }

    @Test
    fun `short password is rejected before the network`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = viewModelWith(password = "1234567")

            viewModel.submit()

            assertEquals(FieldError.PasswordTooShort, viewModel.state.value.passwordError)
            assertEquals(AuthField.Password, viewModel.state.value.focusTarget)
            assertEquals(0, repository.registerCalls)
        }

    @Test
    fun `duplicate email is shown inline under the email field`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.registerFailure =
                FirebaseAuthException("ERROR_EMAIL_ALREADY_IN_USE", "raw server text")
            val viewModel = viewModelWith()

            viewModel.submit()

            val state = viewModel.state.value
            assertEquals(FieldError.EmailInUse, state.emailError)
            assertEquals(AuthField.Email, state.focusTarget)
            assertNull(state.formError)
            assertFalse(state.isSubmitting)
        }

    @Test
    fun `weak password from the server lands on the password field`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.registerFailure = FirebaseAuthException("ERROR_WEAK_PASSWORD", "raw")
            val viewModel = viewModelWith()

            viewModel.submit()

            assertEquals(FieldError.PasswordTooShort, viewModel.state.value.passwordError)
            assertEquals(AuthField.Password, viewModel.state.value.focusTarget)
        }

    @Test
    fun `network failure raises the retry snackbar and keeps the typed values`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.registerFailure = IOException("offline")
            val viewModel = viewModelWith()

            viewModel.submit()

            val state = viewModel.state.value
            assertEquals(1, state.networkErrorId)
            assertNull(state.formError)
            assertFalse(state.isSubmitting)
            assertEquals("Ada Lovelace", state.name)
            assertEquals("ada@example.com", state.email)
        }

    @Test
    fun `unknown failure gets a banner rather than a field error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            repository.registerFailure = IllegalStateException("boom")
            val viewModel = viewModelWith()

            viewModel.submit()

            assertEquals(AuthError.Unknown, viewModel.state.value.formError)
            assertNull(viewModel.state.value.emailError)
        }

    @Test
    fun `loading flag is set while the profile write is in flight`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val gate = CompletableDeferred<Unit>()
            repository.gate = gate
            val viewModel = viewModelWith()

            viewModel.submit()
            assertTrue(viewModel.state.value.isSubmitting)

            gate.complete(Unit)
            assertTrue(viewModel.state.value.isSubmitting)
            assertEquals("Ada Lovelace", repository.lastRegisterName)
        }

    @Test
    fun `success marks the one-shot welcome flag`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = viewModelWith()

            viewModel.submit()

            assertTrue(welcomeSignal.pending.value)
            assertNull(viewModel.state.value.formError)
        }

    @Test
    fun `name is trimmed before it is stored`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModelWith(name = "  Ada Lovelace  ")

        viewModel.submit()

        assertEquals("Ada Lovelace", repository.lastRegisterName)
    }

    @Test
    fun `blur validates without a submit`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = RegisterViewModel(repository, welcomeSignal).apply {
            onEmailChange("ada@")
        }

        viewModel.onEmailBlur()

        assertEquals(FieldError.EmailInvalid, viewModel.state.value.emailError)
        assertEquals(0, repository.registerCalls)
    }

    @Test
    fun `typing in a field clears that field's error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = RegisterViewModel(repository, welcomeSignal)
            viewModel.submit()
            assertEquals(FieldError.NameBlank, viewModel.state.value.nameError)

            viewModel.onNameChange("Ada")

            assertNull(viewModel.state.value.nameError)
        }
}
