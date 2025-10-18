
package com.scoreretriever.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scoreretriever.R
import com.scoreretriever.presentation.component.ComponentFactory
import com.scoreretriever.presentation.component.ComponentType
import com.scoreretriever.presentation.state.CreditScoreUiState
import com.scoreretriever.presentation.viewmodel.CreditScoreViewModel

/**
 * Main screen for displaying credit score information.
 *
 * This screen follows MVVM pattern and Compose best practices:
 * - Observes ViewModel state using StateFlow
 * - Handles all UI states (Loading, Success, Error)
 * - Uses ComponentFactory to render different component types
 * - Provides component type selector for demonstration
 *
 * The screen is stateless and derives all UI from ViewModel state.
 * This makes it easy to test and ensures single source of truth.
 *
 * @param viewModel The credit score ViewModel (injected by Hilt via hiltViewModel())
 * @param modifier Optional modifier for the screen root
 */
@Composable
fun CreditScoreScreen(
    viewModel: CreditScoreViewModel = hiltViewModel(),
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
            when (uiState) {
                is CreditScoreUiState.Loading -> {
                    LoadingContent()
                }

                is CreditScoreUiState.Success -> {
                    val creditScore = (uiState as CreditScoreUiState.Success).creditScore
                    SuccessContent(
                        creditScore = creditScore,
                        selectedComponentType = selectedComponentType,
                        onComponentTypeSelected = viewModel::onComponentTypeSelected
                    )
                }

                is CreditScoreUiState.Error -> {
                    val errorMessage = (uiState as CreditScoreUiState.Error).message
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
            text = stringResource(R.string.loading_credit_score),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Success state UI.
 * Displays the credit score using the selected component implementation
 * and provides a component type selector.
 */
@Composable
private fun SuccessContent(
    creditScore: com.scoreretriever.domain.model.CreditScore,
    selectedComponentType: ComponentType,
    onComponentTypeSelected: (ComponentType) -> Unit
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
            creditScore = creditScore,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Component type selector
        ComponentTypeSelector(
            selectedType = selectedComponentType,
            onTypeSelected = onComponentTypeSelected
        )
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

/**
 * Component type selector UI.
 * Displays chips for each component type to allow switching implementations.
 * Currently only PLACEHOLDER is functional; others will be added in later phases.
 */
@Composable
private fun ComponentTypeSelector(
    selectedType: ComponentType,
    onTypeSelected: (ComponentType) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.component_type),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComponentType.entries.forEach { type ->
                FilterChip(
                    selected = type == selectedType,
                    onClick = { onTypeSelected(type) },
                    label = { Text(stringResource(type.displayNameRes)) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                if (type != ComponentType.entries.last()) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.placeholder_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
    }
}
