package com.neuroverse.auth.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `password rule is eight characters`() {
        // Guards the AGENTS.md auth rule against a silent edit.
        assertEquals(8, Validators.MinPasswordLength)
    }

    @Test
    fun `name rejects blank and whitespace only`() {
        assertEquals(FieldError.NameBlank, Validators.validateName(""))
        assertEquals(FieldError.NameBlank, Validators.validateName("   "))
        assertEquals(FieldError.NameBlank, Validators.validateName("\t\n"))
    }

    @Test
    fun `name accepts a real name and ignores surrounding space`() {
        assertNull(Validators.validateName("Ada"))
        assertNull(Validators.validateName("  Ada Lovelace  "))
    }

    @Test
    fun `email rejects blank and malformed input`() {
        assertEquals(FieldError.EmailInvalid, Validators.validateEmail(""))
        assertEquals(FieldError.EmailInvalid, Validators.validateEmail("   "))
        assertEquals(FieldError.EmailInvalid, Validators.validateEmail("ada"))
        assertEquals(FieldError.EmailInvalid, Validators.validateEmail("ada@"))
        assertEquals(FieldError.EmailInvalid, Validators.validateEmail("ada@example"))
        assertEquals(FieldError.EmailInvalid, Validators.validateEmail("ada lovelace@example.com"))
        assertEquals(FieldError.EmailInvalid, Validators.validateEmail("ada@.com"))
        assertEquals(FieldError.EmailInvalid, Validators.validateEmail("@example.com"))
    }

    @Test
    fun `email accepts normal and tagged addresses`() {
        assertNull(Validators.validateEmail("ada@example.com"))
        assertNull(Validators.validateEmail("first.last+tag@sub.example.co.uk"))
        assertNull(Validators.validateEmail("  ada@example.com  "))
    }

    @Test
    fun `new password rejects anything shorter than eight`() {
        assertEquals(FieldError.PasswordTooShort, Validators.validateNewPassword(""))
        assertEquals(FieldError.PasswordTooShort, Validators.validateNewPassword("1234567"))
        assertNull(Validators.validateNewPassword("12345678"))
        assertNull(Validators.validateNewPassword("correct horse battery"))
    }

    @Test
    fun `login password only rejects empty`() {
        // A short stored password must still be able to log in; the 8-character
        // rule is a creation-time policy, not a login gate.
        assertEquals(FieldError.PasswordBlank, Validators.validateExistingPassword(""))
        assertNull(Validators.validateExistingPassword("short"))
    }
}
