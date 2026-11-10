package com.neuroverse.auth.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neuroverse.auth.di.ServiceLocator
import com.neuroverse.auth.ui.screens.HomeScreen
import com.neuroverse.auth.ui.screens.LoginScreen
import com.neuroverse.auth.ui.screens.PlaceholderScreen
import com.neuroverse.auth.ui.screens.RegisterScreen
import com.neuroverse.auth.ui.screens.SplashScreen
import com.neuroverse.auth.ui.screens.WelcomeScreen
import com.neuroverse.auth.ui.theme.Motion
import com.neuroverse.auth.viewmodel.LoginViewModel
import com.neuroverse.auth.viewmodel.RegisterViewModel
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
    showWelcome: Boolean,
    onWelcomeShown: () -> Unit,
    onSignOut: () -> Unit,
) {
    when (state) {
        SessionState.Loading -> SplashScreen()
        SessionState.SignedOut -> AuthNavHost()
        is SessionState.SignedIn -> MainNavHost(
            email = state.user.email.orEmpty(),
            showWelcome = showWelcome,
            onWelcomeShown = onWelcomeShown,
            onSignOut = onSignOut,
        )
    }
}

@Composable
private fun AuthNavHost(navController: NavHostController = rememberNavController()) {
    // 16dp of travel, converted once because transaction lambdas are not composable.
    val slide = with(LocalDensity.current) { 16.dp.roundToPx() }

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME,
        enterTransition = {
            fadeIn(tween(Motion.Screen, easing = Motion.Standard)) +
                slideInHorizontally(tween(Motion.Screen, easing = Motion.Standard)) { slide }
        },
        exitTransition = {
            fadeOut(tween(Motion.Screen)) +
                slideOutHorizontally(tween(Motion.Screen)) { -slide }
        },
        popEnterTransition = {
            fadeIn(tween(Motion.Screen)) +
                slideInHorizontally(tween(Motion.Screen)) { -slide }
        },
        popExitTransition = {
            fadeOut(tween(Motion.Screen)) +
                slideOutHorizontally(tween(Motion.Screen)) { slide }
        },
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onCreateAccount = { navController.navigate(Routes.REGISTER) },
                onLogIn = { navController.navigate(Routes.LOGIN) },
            )
        }
        composable(Routes.REGISTER) {
            RegisterRoute(
                onBack = { navController.popBackStack() },
                onLogIn = {
                    // Swap rather than stack, so the two forms cannot pile up.
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.LOGIN) {
            LoginRoute(
                onBack = { navController.popBackStack() },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onCreateAccount = {
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
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
private fun RegisterRoute(
    onBack: () -> Unit,
    onLogIn: () -> Unit,
) {
    val factory = remember {
        RegisterViewModel.factory(
            repository = ServiceLocator.authRepository,
            welcomeSignal = ServiceLocator.welcomeSignal,
        )
    }
    val viewModel: RegisterViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    RegisterScreen(
        state = state,
        onSubmit = viewModel::submit,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onNameBlur = viewModel::onNameBlur,
        onEmailBlur = viewModel::onEmailBlur,
        onPasswordBlur = viewModel::onPasswordBlur,
        onRetry = viewModel::onRetry,
        onBack = onBack,
        onLogIn = onLogIn,
    )
}

@Composable
private fun LoginRoute(
    onBack: () -> Unit,
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    val factory = remember { LoginViewModel.factory(ServiceLocator.authRepository) }
    val viewModel: LoginViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        onSubmit = viewModel::submit,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onEmailBlur = viewModel::onEmailBlur,
        onPasswordBlur = viewModel::onPasswordBlur,
        onRetry = viewModel::onRetry,
        onBack = onBack,
        onForgotPassword = onForgotPassword,
        onCreateAccount = onCreateAccount,
    )
}

@Composable
private fun MainNavHost(
    email: String,
    showWelcome: Boolean,
    onWelcomeShown: () -> Unit,
    onSignOut: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                email = email,
                showWelcome = showWelcome,
                onWelcomeShown = onWelcomeShown,
                onSignOut = onSignOut,
            )
        }
    }
}
