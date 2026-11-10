package com.neuroverse.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neuroverse.auth.di.ServiceLocator
import com.neuroverse.auth.navigation.AppNavHost
import com.neuroverse.auth.ui.theme.NeuroVerseAuthTheme
import com.neuroverse.auth.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            NeuroVerseAuthTheme {
                val factory = remember {
                    SessionViewModel.factory(ServiceLocator.authRepository)
                }
                val sessionViewModel: SessionViewModel = viewModel(factory = factory)
                val sessionState by sessionViewModel.state.collectAsStateWithLifecycle()

                val welcomeSignal = ServiceLocator.welcomeSignal
                val welcomePending by welcomeSignal.pending.collectAsStateWithLifecycle()

                AppNavHost(
                    state = sessionState,
                    showWelcome = welcomePending,
                    onWelcomeShown = welcomeSignal::consume,
                    onSignOut = {
                        // Logout clears every local auth flag we own. Screen state
                        // dies with the nav graph; this one is app-wide, so it has
                        // to be reset by hand or the next user sees the greeting.
                        welcomeSignal.consume()
                        sessionViewModel.signOut()
                    },
                )
            }
        }
    }
}
