
package com.scoreretriever.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoreretriever.R
import com.scoreretriever.domain.model.Score
import com.scoreretriever.presentation.component.ComponentFactory
import com.scoreretriever.presentation.component.ComponentType
import com.scoreretriever.presentation.state.ScoreUiState
import com.scoreretriever.presentation.viewmodel.ScoreViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.rememberHazeState

/**
 * Main screen for displaying score information.
 *
 * This screen follows MVVM pattern and Compose best practices:
 * - Observes ViewModel state using StateFlow
 * - Handles all UI states (Loading, Success, Error)
 * - Uses ComponentFactory to render different component types
 * - Bottom navigation bar for switching between visualization types
 *
 * The screen is stateless and derives all UI from ViewModel state.
 * This makes it easy to test and ensures single source of truth.
 *
 * Navigation bar includes three visualization options:
 * - Simple 2D (currently PLACEHOLDER implementation)
 * - Complex 2D (ENHANCED_2D - to be implemented)
 * - True 3D (OPENGL_3D - to be implemented)
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
    val hazeState = rememberHazeState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xE0202020)
            ) {
                NavigationBarItem(
                    selected = selectedComponentType == ComponentType.PLACEHOLDER,
                    onClick = { viewModel.onComponentTypeSelected(ComponentType.PLACEHOLDER) },
                    icon = { Text("📊", fontSize = 24.sp) },
                    label = { Text(stringResource(R.string.nav_simple_2d)) }
                )
                NavigationBarItem(
                    selected = selectedComponentType == ComponentType.ENHANCED_2D,
                    onClick = { viewModel.onComponentTypeSelected(ComponentType.ENHANCED_2D) },
                    icon = { Text("🎨", fontSize = 24.sp) },
                    label = { Text(stringResource(R.string.nav_complex_2d)) }
                )
                NavigationBarItem(
                    selected = selectedComponentType == ComponentType.OPENGL_3D,
                    onClick = { viewModel.onComponentTypeSelected(ComponentType.OPENGL_3D) },
                    icon = { Text("🎯", fontSize = 24.sp) },
                    label = { Text(stringResource(R.string.nav_true_3d)) }
                )
            }
        }
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
                modifier = Modifier.fillMaxSize()
                    .hazeSource(hazeState), // Background participates in the haze field
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
                        selectedComponentType = selectedComponentType,
                        hazeState
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
 * Displays a larger circular progress indicator with enhanced text.
 */
@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Larger spinning wheel
        CircularProgressIndicator(
            modifier = Modifier.size(96.dp),
            strokeWidth = 12.dp,
            color = Color.DarkGray.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.loading_score),
            fontSize = 20.sp,
            color = Color.DarkGray.copy(alpha = 0.9f)
        )
    }
}

/**
 * Success state UI.
 * Displays the score using the selected component implementation.
 * Navigation bar provides component type selection at bottom of screen.
 */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun SuccessContent(
    score: Score,
    selectedComponentType: ComponentType,
    hazeState: HazeState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(width = 3.dp, color = Color.White, shape = RectangleShape),
        contentAlignment = Alignment.Center
    ) {

        // Create and display the selected component (blur foreground)
        val component = remember(selectedComponentType) {
            ComponentFactory.create(selectedComponentType)
        }

        component.Content(
            score = score,
            modifier = Modifier,
            hazeState = hazeState
        )
    }
}


/**
 * Error state UI.
 * Displays error message with a retry button in a semi-transparent container.
 */
@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Semi-transparent dark gray rounded rectangle container
        Column(
            modifier = Modifier
                .background(
                    color = Color(0xCC303030),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Network error icon (text emoji)
            Text(
                text = "⚠️",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.error_oops),
                fontSize = 28.sp,
                color = Color(0xFFFFB800)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = message,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.retry),
                    fontSize = 16.sp
                )
            }
        }
    }
}

