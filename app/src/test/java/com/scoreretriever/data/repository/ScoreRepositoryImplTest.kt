package com.scoreretriever.data.repository

import app.cash.turbine.test
import com.scoreretriever.data.api.ScoreApi
import com.scoreretriever.data.api.dto.ScoreInfoDto
import com.scoreretriever.data.api.dto.ScoreResponseDto
import com.scoreretriever.domain.model.ErrorType
import com.scoreretriever.domain.model.Result
import com.scoreretriever.domain.model.Score
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

/**
 * Unit tests for ScoreRepositoryImpl.
 *
 * Tests cover:
 * - Successful API responses
 * - Network errors (IOException)
 * - HTTP errors (HttpException)
 * - JSON parsing errors
 * - Domain validation errors
 * - Unexpected errors
 * - Flow emission
 */
class ScoreRepositoryImplTest {

    private lateinit var api: ScoreApi
    private lateinit var repository: ScoreRepositoryImpl

    @Before
    fun setup() {
        api = mockk()
        repository = ScoreRepositoryImpl(api)
    }

    @Test
    fun `getScore returns Success when API call succeeds`() = runTest {
        // Given
        val scoreInfo = ScoreInfoDto(
            score = 514,
            maxScoreValue = 700
        )
        val responseDto = ScoreResponseDto(
            scoreInfo = scoreInfo,
            accountIDVStatus = null,
            dashboardStatus = null,
            personaType = null
        )
        coEvery { api.getScore() } returns responseDto

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val score = (result as Result.Success).data
            assertEquals(514, score.score)
            assertEquals(700, score.maxScore)
            awaitComplete()
        }
    }

    @Test
    fun `getScore returns NetworkError when IOException occurs`() = runTest {
        // Given
        coEvery { api.getScore() } throws IOException("Network failure")

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            val error = result as Result.Error
            assertTrue(error.errorType is ErrorType.NetworkError)
            assertNotNull(error.exception)
            assertTrue(error.exception is IOException)
            awaitComplete()
        }
    }

    @Test
    fun `getScore returns ServerError when HttpException occurs`() = runTest {
        // Given
        val httpException = mockk<HttpException>(relaxed = true)
        coEvery { api.getScore() } throws httpException

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            val error = result as Result.Error
            assertTrue(error.errorType is ErrorType.ServerError)
            assertNotNull(error.exception)
            awaitComplete()
        }
    }

    @Test
    fun `getScore returns DataFormatError when JsonSyntaxException occurs`() = runTest {
        // Given
        val jsonException = com.google.gson.JsonSyntaxException("Invalid JSON")
        coEvery { api.getScore() } throws jsonException

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            val error = result as Result.Error
            assertTrue(error.errorType is ErrorType.DataFormatError)
            assertNotNull(error.exception)
            awaitComplete()
        }
    }

    @Test
    fun `getScore returns ValidationError when domain model validation fails`() = runTest {
        // Given - Invalid score data (score > maxScore)
        val scoreInfo = ScoreInfoDto(
            score = 800, // Invalid: exceeds max
            maxScoreValue = 700
        )
        val responseDto = ScoreResponseDto(
            scoreInfo = scoreInfo,
            accountIDVStatus = null,
            dashboardStatus = null,
            personaType = null
        )
        coEvery { api.getScore() } returns responseDto

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            val error = result as Result.Error
            assertTrue(error.errorType is ErrorType.ValidationError)
            assertTrue(error.exception is IllegalArgumentException)
            awaitComplete()
        }
    }

    @Test
    fun `getScore returns ValidationError when maxScore is zero`() = runTest {
        // Given
        val scoreInfo = ScoreInfoDto(
            score = 100,
            maxScoreValue = 0 // Invalid
        )
        val responseDto = ScoreResponseDto(
            scoreInfo = scoreInfo,
            accountIDVStatus = null,
            dashboardStatus = null,
            personaType = null
        )
        coEvery { api.getScore() } returns responseDto

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            val error = result as Result.Error
            assertTrue(error.errorType is ErrorType.ValidationError)
            val validationError = error.errorType as ErrorType.ValidationError
            assertTrue(validationError.details.contains("Max score must be greater than 0"))
            awaitComplete()
        }
    }

    @Test
    fun `getScore returns UnexpectedError for other exceptions`() = runTest {
        // Given
        val unexpectedException = RuntimeException("Unexpected error")
        coEvery { api.getScore() } throws unexpectedException

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Error)
            val error = result as Result.Error
            assertTrue(error.errorType is ErrorType.UnexpectedError)
            assertNotNull(error.exception)
            awaitComplete()
        }
    }

    @Test
    fun `getScore maps DTO to domain model correctly`() = runTest {
        // Given
        val scoreInfo = ScoreInfoDto(
            score = 650,
            maxScoreValue = 850
        )
        val responseDto = ScoreResponseDto(
            scoreInfo = scoreInfo,
            accountIDVStatus = "PASS",
            dashboardStatus = "ACTIVE",
            personaType = "CUSTOMER"
        )
        coEvery { api.getScore() } returns responseDto

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val score = (result as Result.Success).data
            assertEquals(650, score.score)
            assertEquals(850, score.maxScore)
            assertEquals(0.765f, score.percentage, 0.001f)
            awaitComplete()
        }
    }

    @Test
    fun `getScore with minimum valid values succeeds`() = runTest {
        // Given
        val scoreInfo = ScoreInfoDto(
            score = 0,
            maxScoreValue = 1
        )
        val responseDto = ScoreResponseDto(
            scoreInfo = scoreInfo,
            accountIDVStatus = null,
            dashboardStatus = null,
            personaType = null
        )
        coEvery { api.getScore() } returns responseDto

        // When/Then
        repository.getScore().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val score = (result as Result.Success).data
            assertEquals(0, score.score)
            assertEquals(1, score.maxScore)
            awaitComplete()
        }
    }
}
