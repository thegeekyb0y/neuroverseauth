package com.neuroverse.auth.domain

/**
 * Field-level failures. Pure enum so it can live in unit tests and be mapped to
 * strings only at the UI edge.
 */
enum class FieldError {
    NameBlank,
    EmailInvalid,
    PasswordTooShort,
    PasswordBlank,
}

/**
 * All input rules for the auth forms.
 *
 * Pure Kotlin on purpose: no Android imports, no I/O, no logging. That keeps the
 * rules testable on the JVM and guarantees they cannot leak a password anywhere.
 */
object Validators {

    /** Product rule: 8 characters. Firebase's own floor is lower, so this is the stricter one. */
    const val MinPasswordLength = 8

    /**
     * Deliberately permissive. Full RFC 5322 validation is not worth it: Firebase
     * does the authoritative check, this only catches obvious typos before a
     * network round trip.
     */
    private val EmailPattern =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    fun validateName(value: String): FieldError? =
        if (value.trim().isEmpty()) FieldError.NameBlank else null

    fun validateEmail(value: String): FieldError? {
        val trimmed = value.trim()
        return if (trimmed.isEmpty() || !EmailPattern.matches(trimmed)) {
            FieldError.EmailInvalid
        } else {
            null
        }
    }

    /** Registration: enforce the product minimum. */
    fun validateNewPassword(value: String): FieldError? =
        if (value.length < MinPasswordLength) FieldError.PasswordTooShort else null

    /**
     * Login: only require that something was typed.
     *
     * The 8-character rule is a creation-time policy. Applying it here would lock
     * out any account made before the rule changed, and it tells an attacker
     * something about the stored password's shape.
     */
    fun validateExistingPassword(value: String): FieldError? =
        if (value.isEmpty()) FieldError.PasswordBlank else null
}
