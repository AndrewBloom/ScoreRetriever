
package com.scoreretriever.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoreretriever.R
import com.scoreretriever.presentation.component.ComponentFactory
import com.scoreretriever.presentation.component.ComponentType
import com.scoreretriever.presentation.state.ScoreUiState
import com.scoreretriever.presentation.viewmodel.ScoreViewModel

/**
 * Main screen for displaying score information.
 *
 * This screen follows MVVM pattern and Compose best practices:
 * - Observes ViewModel state using StateFlow
 * - Handles all UI states (Loading, Success, Error)
 * - Uses ComponentFactory to render different component types
 *
 * The screen is stateless and derives all UI from ViewModel state.
 * This makes it easy to test and ensures single source of truth.
 *
 * Note: Component type selector has been removed. Navigation bar will be
 * added in a future branch to switch between component implementations.
 *
 * @param viewModel The score ViewModel (injected by Hilt via hiltViewModel())
 * @param modifier Optional modifier for the screen root
 */
@Composable
fun ScoreScreen(
    viewModel: ScoreViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    // Collect UI state with lifecycle awareness
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedComponentType by viewModel.selectedComponentType.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.selection),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            when (uiState) {
                is ScoreUiState.Loading -> {
                    LoadingContent()
                }

                is ScoreUiState.Success -> {
                    val score = (uiState as ScoreUiState.Success).score
                    SuccessContent(
                        score = score,
                        selectedComponentType = selectedComponentType
                    )
                }

                is ScoreUiState.Error -> {
                    val errorMessage = (uiState as ScoreUiState.Error).message
                    ErrorContent(
                        message = errorMessage,
                        onRetry = viewModel::onRetry
                    )
                }
            }
        }
    }
}

/**
 * Loading state UI.
 * Displays a circular progress indicator with loading text.
 */
@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.loading_score),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Success state UI.
 * Displays the score using the selected component implementation.
 * Component type selector removed - will be replaced with navigation bar in future branch.
 * Maintains original vertical positioning with bottom padding to match previous layout.
 */
@Composable
private fun SuccessContent(
    score: com.scoreretriever.domain.model.Score,
    selectedComponentType: ComponentType
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Create and display the selected component
        val component = remember(selectedComponentType) {
            ComponentFactory.create(selectedComponentType)
        }

        component.Content(
            score = score,
            modifier = Modifier.weight(1f)
        )

        // Spacer to maintain original component position
        // (approximates space previously occupied by selector UI)
        Spacer(modifier = Modifier.height(260.dp))
    }
}

/**
 * Error state UI.
 * Displays error message with a retry button.
 */
@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.error_oops),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Text(stringResource(id = R.string.retry))
        }
    }
}

