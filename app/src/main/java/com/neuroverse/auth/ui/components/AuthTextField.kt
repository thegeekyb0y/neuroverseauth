package com.neuroverse.auth.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

/**
 * The one text field shape used by both auth forms.
 *
 * Focus loss is reported through [onBlur] so validation happens when the user
 * leaves a field, never on every keystroke.
 */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    autofillType: ContentType? = null,
    focusRequester: FocusRequester? = null,
    onBlur: () -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    // Tracks the previous focus state so we do not report a blur on first layout.
    var wasFocused by remember { mutableStateOf(false) }
    val errorText = error

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier
            )
            .onFocusChanged { state ->
                if (wasFocused && !state.isFocused) onBlur()
                wasFocused = state.isFocused
            }
            .then(
                if (autofillType != null) {
                    Modifier.semantics { contentType = autofillType }
                } else {
                    Modifier
                }
            ),
        enabled = enabled,
        label = { Text(text = label) },
        isError = errorText != null,
        supportingText = if (errorText != null) {
            {
                Text(
                    text = errorText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        // Next and Done both mean "advance the flow", so they share one callback.
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() },
        ),
        shape = MaterialTheme.shapes.medium,
    )
}
