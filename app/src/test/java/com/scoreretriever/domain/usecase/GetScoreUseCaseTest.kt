package com.scoreretriever.domain.usecase

import app.cash.turbine.test
import com.scoreretriever.domain.model.ErrorType
import com.scoreretriever.domain.model.Result
import com.scoreretriever.domain.model.Score
import com.scoreretriever.domain.repository.ScoreRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetScoreUseCase.
 *
 * Tests cover:
 * - Successful score retrieval
 * - Error propagation
 * - Repository interaction
 * - Flow behavior
 */
class GetScoreUseCaseTest {

    private lateinit var repository: ScoreRepository
    private lateinit var useCase: GetScoreUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetScoreUseCase(repository)
    }

    @Test
    fun `invoke returns Success when repository succeeds`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        val result = Result.Success(score)
        every { repository.getScore() } returns flowOf(result)

        // When/Then
        useCase().test {
            val emittedResult = awaitItem()
            assertTrue(emittedResult is Result.Success)
            assertEquals(score, (emittedResult as Result.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns Error when repository fails with NetworkError`() = runTest {
        // Given
        val error = Result.Error(ErrorType.NetworkError)
        every { repository.getScore() } returns flowOf(error)

        // When/Then
        useCase().test {
            val emittedResult = awaitItem()
            assertTrue(emittedResult is Result.Error)
            assertTrue((emittedResult as Result.Error).errorType is ErrorType.NetworkError)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns Error when repository fails with ServerError`() = runTest {
        // Given
        val error = Result.Error(ErrorType.ServerError)
        every { repository.getScore() } returns flowOf(error)

        // When/Then
        useCase().test {
            val emittedResult = awaitItem()
            assertTrue(emittedResult is Result.Error)
            assertTrue((emittedResult as Result.Error).errorType is ErrorType.ServerError)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns Error when repository fails with DataFormatError`() = runTest {
        // Given
        val error = Result.Error(ErrorType.DataFormatError)
        every { repository.getScore() } returns flowOf(error)

        // When/Then
        useCase().test {
            val emittedResult = awaitItem()
            assertTrue(emittedResult is Result.Error)
            assertTrue((emittedResult as Result.Error).errorType is ErrorType.DataFormatError)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns Error when repository fails with ValidationError`() = runTest {
        // Given
        val details = "Invalid score data"
        val error = Result.Error(ErrorType.ValidationError(details))
        every { repository.getScore() } returns flowOf(error)

        // When/Then
        useCase().test {
            val emittedResult = awaitItem()
            assertTrue(emittedResult is Result.Error)
            val errorType = (emittedResult as Result.Error).errorType
            assertTrue(errorType is ErrorType.ValidationError)
            assertEquals(details, (errorType as ErrorType.ValidationError).details)
            awaitComplete()
        }
    }

    @Test
    fun `invoke calls repository getScore method`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        every { repository.getScore() } returns flowOf(Result.Success(score))

        // When
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        // Then
        verify(exactly = 1) { repository.getScore() }
    }

    @Test
    fun `invoke can be called multiple times`() = runTest {
        // Given
        val score = Score(score = 514, maxScore = 700)
        every { repository.getScore() } returns flowOf(Result.Success(score))

        // When - Call twice
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        useCase().test {
            awaitItem()
            awaitComplete()
        }

        // Then - Repository called twice
        verify(exactly = 2) { repository.getScore() }
    }

    @Test
    fun `invoke returns Flow that can be collected`() = runTest {
        // Given
        val score1 = Score(score = 500, maxScore = 700)
        val score2 = Score(score = 600, maxScore = 700)
        every { repository.getScore() } returns flowOf(
            Result.Success(score1),
            Result.Success(score2)
        )

        // When/Then
        useCase().test {
            val result1 = awaitItem()
            assertEquals(score1, (result1 as Result.Success).data)

            val result2 = awaitItem()
            assertEquals(score2, (result2 as Result.Success).data)

            awaitComplete()
        }
    }
}
