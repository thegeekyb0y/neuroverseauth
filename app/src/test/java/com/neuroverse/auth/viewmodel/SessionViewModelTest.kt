package com.neuroverse.auth.viewmodel

import com.neuroverse.auth.MainDispatcherRule
import com.neuroverse.auth.data.AuthUser
import com.neuroverse.auth.fakes.FakeAuthRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SessionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAuthRepository()
    private val user = AuthUser(uid = "uid-1", email = "ada@example.com", displayName = "Ada")

    @Test
    fun `initial state is loading before anything is collected`() {
        val viewModel = SessionViewModel(repository)

        assertEquals(SessionState.Loading, viewModel.state.value)
    }

    @Test
    fun `cold start with no session opens the welcome screen`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = SessionViewModel(repository)
            val states = mutableListOf<SessionState>()
            backgroundScope.launch(mainDispatcherRule.testDispatcher) {
                viewModel.state.toList(states)
            }

            runCurrent()

            val signedOut = states.filterIsInstance<SessionState.SignedOut>().last()
            assertEquals(AuthEntry.Welcome, signedOut.entry)
        }

    @Test
    fun `a signed in user is mapped to signed in`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = SessionViewModel(repository)
            val states = mutableListOf<SessionState>()
            backgroundScope.launch(mainDispatcherRule.testDispatcher) {
                viewModel.state.toList(states)
            }

            repository.sessionFlow.value = user
            runCurrent()

            val signedIn = states.filterIsInstance<SessionState.SignedIn>().last()
            assertEquals(user, signedIn.user)
        }

    @Test
    fun `logout after a session opens the login screen`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = SessionViewModel(repository)
            val states = mutableListOf<SessionState>()
            backgroundScope.launch(mainDispatcherRule.testDispatcher) {
                viewModel.state.toList(states)
            }

            // Sign in, then the session goes away (user logout or a revoked token).
            repository.sessionFlow.value = user
            runCurrent()
            repository.sessionFlow.value = null
            runCurrent()

            val signedOut = states.filterIsInstance<SessionState.SignedOut>().last()
            assertEquals(AuthEntry.Login, signedOut.entry)
        }

    @Test
    fun `logout is recognised even without the view model initiating it`() =
        runTest(mainDispatcherRule.testDispatcher) {
            // Covers a revoked token or a user deleted from the console: the
            // session flow changes on its own, with no signOut() call.
            val viewModel = SessionViewModel(repository)
            val states = mutableListOf<SessionState>()
            backgroundScope.launch(mainDispatcherRule.testDispatcher) {
                viewModel.state.toList(states)
            }

            repository.sessionFlow.value = user
            runCurrent()
            repository.sessionFlow.value = null
            runCurrent()

            val last = states.last()
            assertTrue(last is SessionState.SignedOut && last.entry == AuthEntry.Login)
        }

    @Test
    fun `sign out delegates to the repository`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = SessionViewModel(repository)

        viewModel.signOut()

        assertEquals(1, repository.signOutCalls)
    }
}
