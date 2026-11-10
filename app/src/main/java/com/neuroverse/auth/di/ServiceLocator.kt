package com.neuroverse.auth.di

import com.neuroverse.auth.data.AuthRepository
import com.neuroverse.auth.data.FirebaseAuthRepository
import com.neuroverse.auth.viewmodel.WelcomeSignal

/**
 * Single app module, so a hand-rolled service locator is enough. No DI library
 * is added.
 */
object ServiceLocator {
    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }

    /**
     * Shared by registration (writer) and Home (reader). In-memory only; a fresh
     * process starts with no pending welcome.
     */
    val welcomeSignal: WelcomeSignal by lazy { WelcomeSignal() }
}
