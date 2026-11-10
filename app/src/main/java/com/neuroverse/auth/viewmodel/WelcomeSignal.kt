package com.neuroverse.auth.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * One-shot, in-memory flag for "a registration just succeeded".
 *
 * It deliberately is not persisted and not a token: it exists for exactly one
 * snackbar on Home. Marked by registration, consumed by Home, gone.
 */
class WelcomeSignal {

    private val _pending = MutableStateFlow(false)
    val pending: StateFlow<Boolean> = _pending.asStateFlow()

    fun markPending() {
        _pending.value = true
    }

    fun consume() {
        _pending.value = false
    }
}
