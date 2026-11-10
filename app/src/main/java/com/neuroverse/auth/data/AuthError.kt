package com.neuroverse.auth.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import java.io.IOException

/**
 * User-safe failure categories. UI code maps these to strings; raw Firebase
 * messages never reach the screen.
 */
enum class AuthError {
    InvalidEmail,
    WeakPassword,
    EmailAlreadyInUse,
    InvalidCredentials,
    TooManyRequests,
    Network,
    Unknown,
}

fun Throwable.toAuthError(): AuthError = when (this) {
    // Network failures are a FirebaseException but not a FirebaseAuthException,
    // so they need their own branch or they degrade to Unknown.
    is FirebaseNetworkException -> AuthError.Network
    is FirebaseAuthException -> when (errorCode) {
        "ERROR_INVALID_EMAIL" -> AuthError.InvalidEmail
        "ERROR_WEAK_PASSWORD" -> AuthError.WeakPassword
        "ERROR_EMAIL_ALREADY_IN_USE" -> AuthError.EmailAlreadyInUse
        // With email enumeration protection on, both a wrong password and an
        // unknown account surface as invalid credentials. Keep them merged.
        "ERROR_INVALID_CREDENTIAL",
        "ERROR_WRONG_PASSWORD",
        "ERROR_USER_NOT_FOUND" -> AuthError.InvalidCredentials
        "ERROR_TOO_MANY_REQUESTS" -> AuthError.TooManyRequests
        "ERROR_NETWORK_REQUEST_FAILED" -> AuthError.Network
        else -> AuthError.Unknown
    }
    is IOException -> AuthError.Network
    else -> AuthError.Unknown
}
