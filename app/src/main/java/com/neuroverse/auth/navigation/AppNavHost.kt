package com.neuroverse.auth.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neuroverse.auth.ui.screens.HomeScreen
import com.neuroverse.auth.ui.screens.PlaceholderScreen
import com.neuroverse.auth.ui.screens.SplashScreen
import com.neuroverse.auth.ui.screens.WelcomeScreen
import com.neuroverse.auth.viewmodel.SessionState

/**
 * Root gate. Exactly one branch is composed at a time, so an authenticated
 * screen can never be reached while signed out, and no destination is chosen
 * while the session is still Loading (that would flash Login for a signed-in
 * user).
 */
@Composable
fun AppNavHost(
    state: SessionState,
    onSignOut: () -> Unit,
) {
    when (state) {
        SessionState.Loading -> SplashScreen()
        SessionState.SignedOut -> AuthNavHost()
        is SessionState.SignedIn -> MainNavHost(
            email = state.user.email.orEmpty(),
            onSignOut = onSignOut,
        )
    }
}

@Composable
private fun AuthNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onCreateAccount = { navController.navigate(Routes.REGISTER) },
                onLogIn = { navController.navigate(Routes.LOGIN) },
            )
        }
        composable(Routes.LOGIN) {
            PlaceholderScreen(
                title = "Log in",
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.REGISTER) {
            PlaceholderScreen(
                title = "Create account",
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.FORGOT_PASSWORD) {
            PlaceholderScreen(
                title = "Reset password",
                onBack = { navController.popBackStack() },
            )
        }
    }
}

@Composable
private fun MainNavHost(
    email: String,
    onSignOut: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(email = email, onSignOut = onSignOut)
        }
    }
}
