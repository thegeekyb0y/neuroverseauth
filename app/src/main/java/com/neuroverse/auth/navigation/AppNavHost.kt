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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neuroverse.auth.data.AuthUser
import com.neuroverse.auth.di.ServiceLocator
import com.neuroverse.auth.ui.screens.ForgotPasswordScreen
import com.neuroverse.auth.ui.screens.HomeScreen
import com.neuroverse.auth.ui.screens.LoginScreen
import com.neuroverse.auth.ui.screens.ProfileScreen
import com.neuroverse.auth.ui.screens.RegisterScreen
import com.neuroverse.auth.ui.screens.SplashScreen
import com.neuroverse.auth.ui.screens.WelcomeScreen
import com.neuroverse.auth.ui.theme.Motion
import com.neuroverse.auth.viewmodel.AuthEntry
import com.neuroverse.auth.viewmodel.ForgotPasswordViewModel
import com.neuroverse.auth.viewmodel.LoginViewModel
import com.neuroverse.auth.viewmodel.ProfileViewModel
import com.neuroverse.auth.viewmodel.RegisterViewModel
import com.neuroverse.auth.viewmodel.SessionState

/**
 * Root gate. Exactly one branch is composed at a time, so an authenticated
 * screen can never be reached while signed out, and no destination is chosen
 * while the session is still Loading (that would flash Login for a signed-in
 * user).
 *
 * Signing out drops the whole [MainNavHost] subtree, including its
 * NavController and view model store, so the authenticated back stack does not
 * exist any more. That is why Back cannot return to Home after logout.
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
        is SessionState.SignedOut -> AuthNavHost(startAtLogin = state.entry == AuthEntry.Login)
        is SessionState.SignedIn -> MainNavHost(
            user = state.user,
            showWelcome = showWelcome,
            onWelcomeShown = onWelcomeShown,
            onSignOut = onSignOut,
        )
    }
}

/**
 * Shared 300ms fade plus 16dp slide. Transactions are not composable, so the
 * pixel offset is resolved once per composition.
 */
@Composable
private fun AnimatedNavHost(
    navController: NavHostController,
    startDestination: String,
    builder: NavGraphBuilder.() -> Unit,
) {
    val slide = with(LocalDensity.current) { 16.dp.roundToPx() }

    NavHost(
        navController = navController,
        startDestination = startDestination,
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
        builder = builder,
    )
}

@Composable
private fun AuthNavHost(
    startAtLogin: Boolean,
    navController: NavHostController = rememberNavController(),
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = if (startAtLogin) Routes.LOGIN else Routes.WELCOME,
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
                onBack = {
                    // After a logout Login is the start destination, so there is
                    // nothing to pop. Welcome stays in the graph and becomes the
                    // new root instead.
                    if (!navController.popBackStack()) {
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onCreateAccount = {
                    navController.navigate(Routes.REGISTER) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordRoute(
                // Also used by "Back to login": this screen is only reachable
                // from Login, so popping lands exactly there.
                onBack = { navController.popBackStack() },
            )
        }
    }
}

@Composable
private fun MainNavHost(
    user: AuthUser,
    showWelcome: Boolean,
    onWelcomeShown: () -> Unit,
    onSignOut: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                user = user,
                showWelcome = showWelcome,
                onWelcomeShown = onWelcomeShown,
                onSignOut = onSignOut,
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
            )
        }
        composable(Routes.PROFILE) {
            ProfileRoute(
                user = user,
                onBack = { navController.popBackStack() },
                onSignOut = onSignOut,
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
private fun ForgotPasswordRoute(onBack: () -> Unit) {
    val factory = remember { ForgotPasswordViewModel.factory(ServiceLocator.authRepository) }
    val viewModel: ForgotPasswordViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onSubmit = viewModel::submit,
        onEmailChange = viewModel::onEmailChange,
        onEmailBlur = viewModel::onEmailBlur,
        onRetry = viewModel::onRetry,
        onBack = onBack,
    )
}

@Composable
private fun ProfileRoute(
    user: AuthUser,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
) {
    val factory = remember(user) { ProfileViewModel.factory(user) }
    val viewModel: ProfileViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileScreen(
        state = state,
        onBack = onBack,
        onSignOut = onSignOut,
    )
}
