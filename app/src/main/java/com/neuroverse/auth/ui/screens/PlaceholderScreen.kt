package com.neuroverse.auth.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neuroverse.auth.R
import com.neuroverse.auth.ui.theme.Dimens

/**
 * Temporary screen used by Login, Register and Forgot Password until
 * Milestone 2 and 3 replace them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backLabel = stringResource(R.string.action_back)
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics { contentDescription = backLabel },
                    ) {
                        BackArrow()
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.ScreenGutter),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.placeholder_coming_soon),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Hand-drawn back arrow. Material icons are no longer a transitive dependency of
 * Material 3, and adding an icon dependency is outside this milestone's scope.
 */
@Composable
private fun BackArrow(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(
        modifier = modifier
            .size(24.dp)
            .clearAndSetSemantics { },
    ) {
        val stroke = 2.dp.toPx()
        val w = size.width
        val h = size.height
        drawLine(color, Offset(w * 0.30f, h * 0.50f), Offset(w * 0.76f, h * 0.20f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.30f, h * 0.50f), Offset(w * 0.76f, h * 0.80f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.22f, h * 0.50f), Offset(w * 0.84f, h * 0.50f), stroke, StrokeCap.Round)
    }
}
