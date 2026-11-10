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

                AppNavHost(
                    state = sessionState,
                    onSignOut = sessionViewModel::signOut,
                )
            }
        }
    }
}
