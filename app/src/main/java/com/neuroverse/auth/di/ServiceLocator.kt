package com.neuroverse.auth.di

import com.neuroverse.auth.data.AuthRepository
import com.neuroverse.auth.data.FirebaseAuthRepository

/**
 * Single app module, so a hand-rolled service locator is enough. No DI library
 * is added.
 */
object ServiceLocator {
    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }
}
