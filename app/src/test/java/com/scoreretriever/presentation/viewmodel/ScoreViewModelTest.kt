package com.scoreretriever.presentation.viewmodel

import android.app.Application
import app.cash.turbine.test
import com.scoreretriever.R
import com.scoreretriever.domain.model.ErrorType
import com.scoreretriever.domain.model.Result
import com.scoreretriever.domain.model.Score
import com.scoreretriever.domain.usecase.GetScoreUseCase
import com.scoreretriever.presentation.component.ComponentType
import com.scoreretriever.presentation.state.ScoreUiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ScoreViewModel.
 *
 * Tests cover:
 * - Initial loading state
 * - Successful score fetching
 * - Error handling for all error types
 * - Error message mapping
 * - StateFlow emissions
 * - Component type selection
 * - Retry functionality
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ScoreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getScoreUseCase: GetScoreUseCase
    private lateinit var application: Application
    private lateinit var viewModel: ScoreViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getScoreUseCase = mockk()
        application = mockk(relaxed = true)

        // Mock string resources
        every { application.getString(R.string.error_network) } returns "Network error. Please check your connection."
        every { application.getString(R.string.error_server) } returns "Server error. Please try again later."
        every { application.getString(R.string.error_data_format) } returns "Data format error. Please try again."
        every { application.getString(R.string.error_invalid_data, any()) } returns "Invalid data received: test"
        every { application.getString(R.string.error_unexpected, any()) } returns "An unexpected error occurred: test"

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Given
        every { getScoreUseCase() } returns flowOf() // Never completes

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)

        // Then
        viewModel.uiState.test {
            assertEquals(ScoreUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchScore emits Success state when use case succeeds`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        every { getScoreUseCase() } returns flowOf(Result.Success(score))

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is ScoreUiState.Success)
            assertEquals(score, (state as ScoreUiState.Success).score)
        }
    }

    @Test
    fun `fetchScore emits Error state with NetworkError message`() = runTest {
        // Given
        every { getScoreUseCase() } returns flowOf(Result.Error(ErrorType.NetworkError))

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is ScoreUiState.Error)
            assertEquals(
                "Network error. Please check your connection.",
                (state as ScoreUiState.Error).message
            )
        }
    }

    @Test
    fun `fetchScore emits Error state with ServerError message`() = runTest {
        // Given
        every { getScoreUseCase() } returns flowOf(Result.Error(ErrorType.ServerError))

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is ScoreUiState.Error)
            assertEquals(
                "Server error. Please try again later.",
                (state as ScoreUiState.Error).message
            )
        }
    }

    @Test
    fun `fetchScore emits Error state with DataFormatError message`() = runTest {
        // Given
        every { getScoreUseCase() } returns flowOf(Result.Error(ErrorType.DataFormatError))

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is ScoreUiState.Error)
            assertEquals(
                "Data format error. Please try again.",
                (state as ScoreUiState.Error).message
            )
        }
    }

    @Test
    fun `fetchScore emits Error state with ValidationError message`() = runTest {
        // Given
        val errorDetails = "Max score must be greater than 0"
        every { getScoreUseCase() } returns flowOf(
            Result.Error(ErrorType.ValidationError(errorDetails))
        )

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is ScoreUiState.Error)
            assertEquals(
                "Invalid data received: test",
                (state as ScoreUiState.Error).message
            )
        }
    }

    @Test
    fun `fetchScore emits Error state with UnexpectedError message`() = runTest {
        // Given
        val errorDetails = "Unknown error"
        every { getScoreUseCase() } returns flowOf(
            Result.Error(ErrorType.UnexpectedError(errorDetails))
        )

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is ScoreUiState.Error)
            assertEquals(
                "An unexpected error occurred: test",
                (state as ScoreUiState.Error).message
            )
        }
    }

    @Test
    fun `onComponentTypeSelected updates selectedComponentType`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        every { getScoreUseCase() } returns flowOf(Result.Success(score))
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // When
        viewModel.onComponentTypeSelected(ComponentType.BASIC_2D)

        // Then
        viewModel.selectedComponentType.test {
            assertEquals(ComponentType.BASIC_2D, awaitItem())
        }
    }

    @Test
    fun `initial selectedComponentType is PLACEHOLDER`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        every { getScoreUseCase() } returns flowOf(Result.Success(score))

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // Then
        viewModel.selectedComponentType.test {
            assertEquals(ComponentType.PLACEHOLDER, awaitItem())
        }
    }

    @Test
    fun `onRetry calls fetchScore again`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        every { getScoreUseCase() } returns flowOf(Result.Success(score))
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // When
        viewModel.onRetry()
        advanceUntilIdle()

        // Then - Use case should be called twice (init + retry)
        verify(exactly = 2) { getScoreUseCase() }
    }

    @Test
    fun `fetchScore transitions from Loading to Success`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        every { getScoreUseCase() } returns flowOf(Result.Success(score))

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)

        // Then
        viewModel.uiState.test {
            // Initial state
            assertEquals(ScoreUiState.Loading, awaitItem())

            advanceUntilIdle()

            // Final state after fetch
            val finalState = awaitItem()
            assertTrue(finalState is ScoreUiState.Success)
            assertEquals(score, (finalState as ScoreUiState.Success).score)
        }
    }

    @Test
    fun `fetchScore transitions from Loading to Error`() = runTest {
        // Given
        every { getScoreUseCase() } returns flowOf(Result.Error(ErrorType.NetworkError))

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)

        // Then
        viewModel.uiState.test {
            // Initial state
            assertEquals(ScoreUiState.Loading, awaitItem())

            advanceUntilIdle()

            // Final state after fetch
            val finalState = awaitItem()
            assertTrue(finalState is ScoreUiState.Error)
        }
    }

    @Test
    fun `ViewModel fetches score on initialization`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        every { getScoreUseCase() } returns flowOf(Result.Success(score))

        // When
        viewModel = ScoreViewModel(getScoreUseCase, application)
        advanceUntilIdle()

        // Then
        verify(exactly = 1) { getScoreUseCase() }
    }
}
