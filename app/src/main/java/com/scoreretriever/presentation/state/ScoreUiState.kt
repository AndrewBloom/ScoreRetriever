package com.scoreretriever.presentation.state

import com.scoreretriever.domain.model.Score

/**
 * Sealed interface representing all possible UI states for the score screen.
 *
 * This follows the State pattern and makes state management explicit:
 * - Each state is a distinct type, preventing invalid states
 * - UI can exhaustively handle all states with when expressions
 * - Type-safe: Loading has no data, Success has Score, Error has message
 *
 * Using sealed interface (instead of sealed class) allows:
 * - Data classes to implement the interface
 * - Better structure and extensibility
 * - Clear separation of state types
 */
sealed interface ScoreUiState {
    /**
     * Initial/loading state while fetching score data.
     */
    data object Loading : ScoreUiState

    /**
     * Success state with score data available.
     *
     * @property score The score domain model
     */
    data class Success(val score: Score) : ScoreUiState

    /**
     * Error state when score fetch fails.
     *
     * @property message User-friendly error message to display
     */
    data class Error(val message: String) : ScoreUiState
}
