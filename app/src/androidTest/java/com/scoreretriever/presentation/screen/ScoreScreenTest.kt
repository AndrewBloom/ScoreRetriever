package com.scoreretriever.presentation.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.scoreretriever.domain.model.Score
import com.scoreretriever.presentation.component.ComponentType
import com.scoreretriever.presentation.state.ScoreUiState
import com.scoreretriever.presentation.viewmodel.ScoreViewModel
import com.scoreretriever.ui.theme.ScoreTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented UI tests for ScoreScreen composable.
 *
 * Tests cover:
 * - Loading state UI
 * - Success state UI with score display
 * - Error state UI with error message and retry button
 * - Component type selector
 * - User interactions (retry button, component selection)
 */
class ScoreScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: ScoreViewModel
    private lateinit var uiStateFlow: MutableStateFlow<ScoreUiState>
    private lateinit var componentTypeFlow: MutableStateFlow<ComponentType>

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(ScoreUiState.Loading)
        componentTypeFlow = MutableStateFlow(ComponentType.PLACEHOLDER)

        every { viewModel.uiState } returns uiStateFlow
        every { viewModel.selectedComponentType } returns componentTypeFlow
    }

    @Test
    fun loadingState_showsProgressIndicator() {
        // Given
        uiStateFlow.value = ScoreUiState.Loading

        // When
        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Loading score...").assertIsDisplayed()
    }

    @Test
    fun successState_displaysScore() {
        // Given
        val score = Score(score = 514, maxScore = 700)
        uiStateFlow.value = ScoreUiState.Success(score)

        // When
        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("514").assertIsDisplayed()
        composeTestRule.onNodeWithText("out of 700").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your score is").assertIsDisplayed()
    }

    @Test
    fun successState_showsComponentTypeSelector() {
        // Given
        val score = Score(score = 514, maxScore = 700)
        uiStateFlow.value = ScoreUiState.Success(score)

        // When
        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Component Type:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Placeholder").assertIsDisplayed()
        composeTestRule.onNodeWithText("2D Donut").assertIsDisplayed()
        composeTestRule.onNodeWithText("2.5D Coin").assertIsDisplayed()
        composeTestRule.onNodeWithText("3D OpenGL").assertIsDisplayed()
    }

    @Test
    fun successState_clickingComponentType_callsViewModel() {
        // Given
        val score = Score(score = 514, maxScore = 700)
        uiStateFlow.value = ScoreUiState.Success(score)

        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onNodeWithText("2D Donut").performClick()

        // Then
        verify { viewModel.onComponentTypeSelected(ComponentType.BASIC_2D) }
    }

    @Test
    fun errorState_displaysErrorMessage() {
        // Given
        val errorMessage = "Network error. Please check your connection."
        uiStateFlow.value = ScoreUiState.Error(errorMessage)

        // When
        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Oops!").assertIsDisplayed()
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun errorState_showsRetryButton() {
        // Given
        uiStateFlow.value = ScoreUiState.Error("Some error")

        // When
        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun errorState_clickingRetry_callsViewModel() {
        // Given
        uiStateFlow.value = ScoreUiState.Error("Some error")

        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // When
        composeTestRule.onNodeWithText("Retry").performClick()

        // Then
        verify { viewModel.onRetry() }
    }

    @Test
    fun successState_displaysPlaceholderNote() {
        // Given
        val score = Score(score = 514, maxScore = 700)
        uiStateFlow.value = ScoreUiState.Success(score)

        // When
        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("Note: Only Placeholder is implemented in Phase 1")
            .assertIsDisplayed()
    }

    @Test
    fun successState_withZeroScore_displaysCorrectly() {
        // Given
        val score = Score(score = 0, maxScore = 700)
        uiStateFlow.value = ScoreUiState.Success(score)

        // When
        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("0").assertIsDisplayed()
        composeTestRule.onNodeWithText("out of 700").assertIsDisplayed()
    }

    @Test
    fun successState_withMaxScore_displaysCorrectly() {
        // Given
        val score = Score(score = 700, maxScore = 700)
        uiStateFlow.value = ScoreUiState.Success(score)

        // When
        composeTestRule.setContent {
            ScoreTheme {
                ScoreScreen(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule.onNodeWithText("700").assertIsDisplayed()
        composeTestRule.onNodeWithText("out of 700").assertIsDisplayed()
    }
}
