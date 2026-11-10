package com.neuroverse.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.neuroverse.auth.R
import com.neuroverse.auth.ui.components.BrandMark

/**
 * Shown while the session state is Loading. This is what keeps a signed-in user
 * from seeing the Login screen for a frame on cold start.
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val loadingLabel = stringResource(R.string.loading)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            BrandMark()
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = 3.dp,
                modifier = Modifier.semantics { contentDescription = loadingLabel },
            )
        }
    }
}
