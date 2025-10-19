package com.scoreretriever.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Result sealed class.
 *
 * Tests cover:
 * - Success state creation
 * - Error state creation
 * - Helper properties (isSuccess, isError)
 * - getOrNull() method
 */
class ResultTest {

    @Test
    fun `Success contains data`() {
        // Given
        val score = Score(score = 514, maxScore = 700)

        // When
        val result = Result.Success(score)

        // Then
        assertEquals(score, result.data)
    }

    @Test
    fun `Success isSuccess returns true`() {
        // Given
        val result = Result.Success("data")

        // Then
        assertTrue(result.isSuccess)
        assertFalse(result.isError)
    }

    @Test
    fun `Success getOrNull returns data`() {
        // Given
        val score = Score(score = 514, maxScore = 700)
        val result = Result.Success(score)

        // When
        val data = result.getOrNull()

        // Then
        assertEquals(score, data)
    }

    @Test
    fun `Error contains errorType and exception`() {
        // Given
        val errorType = ErrorType.NetworkError
        val exception = Exception("Network failure")

        // When
        val result = Result.Error(errorType = errorType, exception = exception)

        // Then
        assertEquals(errorType, result.errorType)
        assertEquals(exception, result.exception)
    }

    @Test
    fun `Error can be created without exception`() {
        // Given
        val errorType = ErrorType.ServerError

        // When
        val result = Result.Error(errorType = errorType)

        // Then
        assertEquals(errorType, result.errorType)
        assertNull(result.exception)
    }

    @Test
    fun `Error isError returns true`() {
        // Given
        val result = Result.Error(errorType = ErrorType.NetworkError)

        // Then
        assertTrue(result.isError)
        assertFalse(result.isSuccess)
    }

    @Test
    fun `Error getOrNull returns null`() {
        // Given
        val result: Result<Score> = Result.Error(errorType = ErrorType.NetworkError)

        // When
        val data = result.getOrNull()

        // Then
        assertNull(data)
    }

    @Test
    fun `ValidationError contains details`() {
        // Given
        val details = "Score must be non-negative"
        val errorType = ErrorType.ValidationError(details)

        // When
        val result = Result.Error(errorType = errorType)

        // Then
        assertTrue(result.errorType is ErrorType.ValidationError)
        assertEquals(details, (result.errorType as ErrorType.ValidationError).details)
    }

    @Test
    fun `UnexpectedError contains details`() {
        // Given
        val details = "Unknown error occurred"
        val errorType = ErrorType.UnexpectedError(details)

        // When
        val result = Result.Error(errorType = errorType)

        // Then
        assertTrue(result.errorType is ErrorType.UnexpectedError)
        assertEquals(details, (result.errorType as ErrorType.UnexpectedError).details)
    }

    @Test
    fun `Result can be used in when expression`() {
        // Given
        val successResult: Result<String> = Result.Success("data")
        val errorResult: Result<String> = Result.Error(ErrorType.NetworkError)

        // When/Then
        when (successResult) {
            is Result.Success -> assertTrue(true) // Expected
            is Result.Error -> fail("Should be Success")
        }

        when (errorResult) {
            is Result.Success -> fail("Should be Error")
            is Result.Error -> assertTrue(true) // Expected
        }
    }
}
