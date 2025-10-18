package com.scoreretriever.presentation.state

import com.scoreretriever.domain.model.CreditScore

/**
 * Sealed interface representing all possible UI states for the credit score screen.
 *
 * This follows the State pattern and makes state management explicit:
 * - Each state is a distinct type, preventing invalid states
 * - UI can exhaustively handle all states with when expressions
 * - Type-safe: Loading has no data, Success has CreditScore, Error has message
 *
 * Using sealed interface (instead of sealed class) allows:
 * - Data classes to implement the interface
 * - Better structure and extensibility
 * - Clear separation of state types
 */
sealed interface CreditScoreUiState {
    /**
     * Initial/loading state while fetching credit score data.
     */
    data object Loading : CreditScoreUiState

    /**
     * Success state with credit score data available.
     *
     * @property creditScore The credit score domain model
     */
    data class Success(val creditScore: CreditScore) : CreditScoreUiState

    /**
     * Error state when credit score fetch fails.
     *
     * @property message User-friendly error message to display
     */
    data class Error(val message: String) : CreditScoreUiState
}
