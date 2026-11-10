package com.neuroverse.auth.domain

/**
 * Name presentation rules for a signed-in user.
 *
 * Pure Kotlin so the fallbacks are unit-testable and the screens never have to
 * branch on nullability themselves.
 */
object UserDisplay {

    /**
     * The name to greet the user with: the profile name when it has content,
     * otherwise the local part of the email.
     *
     * Returns null only when both are missing, which the screen turns into a
     * generic greeting rather than an empty string.
     */
    fun greetingName(displayName: String?, email: String?): String? =
        displayName?.trim()?.takeIf { it.isNotEmpty() }
            ?: email?.substringBefore('@')?.trim()?.takeIf { it.isNotEmpty() }

    /**
     * One or two letters for the avatar: "Ada Lovelace" gives "AL", "ada@x.com"
     * gives "A". Empty only when there is nothing to work with.
     */
    fun initials(displayName: String?, email: String?): String {
        val source = greetingName(displayName, email) ?: return ""
        val words = source.split(' ', '-', '_', '.').filter { it.isNotEmpty() }
        return when (words.size) {
            0 -> ""
            1 -> words.first().first().uppercase()
            else -> words[0].first().uppercase() + words[1].first().uppercase()
        }
    }
}
