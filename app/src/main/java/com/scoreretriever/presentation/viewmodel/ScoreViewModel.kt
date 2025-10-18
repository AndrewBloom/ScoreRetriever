package com.scoreretriever.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scoreretriever.R
import com.scoreretriever.domain.model.ErrorType
import com.scoreretriever.domain.model.Result
import com.scoreretriever.domain.usecase.GetCreditScoreUseCase
import com.scoreretriever.presentation.component.ComponentType
import com.scoreretriever.presentation.state.CreditScoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the credit score screen following MVVM pattern.
 *
 * Responsibilities:
 * - Manages UI state (Loading, Success, Error)
 * - Orchestrates business logic via Use Case
 * - Survives configuration changes
 * - Exposes StateFlow for UI to observe
 * - Handles component type selection
 *
 * This ViewModel follows SOLID principles:
 * - Single Responsibility: Only manages credit score UI state
 * - Dependency Inversion: Depends on Use Case abstraction
 * - Open/Closed: Can be extended for new features
 *
 * @property getCreditScoreUseCase Use case for fetching credit score (injected by Hilt)
 * @property application Application context for accessing string resources
 */
@HiltViewModel
class CreditScoreViewModel @Inject constructor(
    private val getCreditScoreUseCase: GetCreditScoreUseCase,
    private val application: Application
) : ViewModel() {

    // Backing property for mutable state (private)
    private val _uiState = MutableStateFlow<CreditScoreUiState>(CreditScoreUiState.Loading)

    /**
     * Public immutable StateFlow for UI to observe.
     * UI collects this using collectAsStateWithLifecycle() in Compose.
     */
    val uiState: StateFlow<CreditScoreUiState> = _uiState.asStateFlow()

    // Backing property for component type selection
    private val _selectedComponentType = MutableStateFlow(ComponentType.PLACEHOLDER)

    /**
     * Currently selected component type for rendering credit score.
     */
    val selectedComponentType: StateFlow<ComponentType> = _selectedComponentType.asStateFlow()

    init {
        // Fetch credit score on ViewModel creation
        fetchCreditScore()
    }

    /**
     * Fetches credit score from the use case.
     *
     * Flow:
     * 1. Sets state to Loading
     * 2. Calls use case
     * 3. Collects result from Flow
     * 4. Updates state based on Result (Success or Error)
     *
     * Runs in viewModelScope (cancelled when ViewModel is cleared).
     */
    fun fetchCreditScore() {
        viewModelScope.launch {
            _uiState.value = CreditScoreUiState.Loading

            getCreditScoreUseCase().collect { result ->
                _uiState.value = when (result) {
                    is Result.Success -> {
                        CreditScoreUiState.Success(result.data)
                    }
                    is Result.Error -> {
                        CreditScoreUiState.Error(mapErrorToMessage(result.errorType))
                    }
                }
            }
        }
    }

    /**
     * Handles component type selection change.
     *
     * @param componentType The new component type to display
     */
    fun onComponentTypeSelected(componentType: ComponentType) {
        _selectedComponentType.value = componentType
    }

    /**
     * Handles retry action from error state.
     *
     * Simply calls fetchCreditScore again.
     */
    fun onRetry() {
        fetchCreditScore()
    }

    /**
     * Maps an ErrorType to a localized user-facing error message.
     *
     * This function keeps error message localization in the presentation layer,
     * maintaining Clean Architecture separation between domain and presentation.
     *
     * @param errorType The error type from the domain layer
     * @return Localized error message string
     */
    private fun mapErrorToMessage(errorType: ErrorType): String {
        return when (errorType) {
            is ErrorType.NetworkError -> {
                application.getString(R.string.error_network)
            }
            is ErrorType.ServerError -> {
                application.getString(R.string.error_server)
            }
            is ErrorType.DataFormatError -> {
                application.getString(R.string.error_data_format)
            }
            is ErrorType.ValidationError -> {
                application.getString(R.string.error_invalid_data, errorType.details)
            }
            is ErrorType.UnexpectedError -> {
                application.getString(R.string.error_unexpected, errorType.details)
            }
        }
    }
}
