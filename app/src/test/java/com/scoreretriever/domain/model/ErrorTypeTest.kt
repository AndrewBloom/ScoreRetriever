package com.scoreretriever.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ErrorType sealed class.
 *
 * Tests cover:
 * - All error type variants
 * - Data error types (ValidationError, UnexpectedError)
 * - Equality and pattern matching
 */
class ErrorTypeTest {

    @Test
    fun `NetworkError is a data object`() {
        // Given
        val error1 = ErrorType.NetworkError
        val error2 = ErrorType.NetworkError

        // Then
        assertSame(error1, error2) // Same instance (data object)
    }

    @Test
    fun `ServerError is a data object`() {
        // Given
        val error1 = ErrorType.ServerError
        val error2 = ErrorType.ServerError

        // Then
        assertSame(error1, error2) // Same instance (data object)
    }

    @Test
    fun `DataFormatError is a data object`() {
        // Given
        val error1 = ErrorType.DataFormatError
        val error2 = ErrorType.DataFormatError

        // Then
        assertSame(error1, error2) // Same instance (data object)
    }

    @Test
    fun `ValidationError contains details`() {
        // Given
        val details = "Max score must be greater than 0"

        // When
        val error = ErrorType.ValidationError(details)

        // Then
        assertEquals(details, error.details)
    }

    @Test
    fun `ValidationError with same details are equal`() {
        // Given
        val error1 = ErrorType.ValidationError("Same message")
        val error2 = ErrorType.ValidationError("Same message")

        // Then
        assertEquals(error1, error2)
    }

    @Test
    fun `ValidationError with different details are not equal`() {
        // Given
        val error1 = ErrorType.ValidationError("Message 1")
        val error2 = ErrorType.ValidationError("Message 2")

        // Then
        assertNotEquals(error1, error2)
    }

    @Test
    fun `UnexpectedError contains details`() {
        // Given
        val details = "Unknown error occurred"

        // When
        val error = ErrorType.UnexpectedError(details)

        // Then
        assertEquals(details, error.details)
    }

    @Test
    fun `UnexpectedError with same details are equal`() {
        // Given
        val error1 = ErrorType.UnexpectedError("Same message")
        val error2 = ErrorType.UnexpectedError("Same message")

        // Then
        assertEquals(error1, error2)
    }

    @Test
    fun `ErrorType can be used in when expression`() {
        // Given
        val errors = listOf(
            ErrorType.NetworkError,
            ErrorType.ServerError,
            ErrorType.DataFormatError,
            ErrorType.ValidationError("test"),
            ErrorType.UnexpectedError("test")
        )

        // When/Then
        errors.forEach { error ->
            when (error) {
                is ErrorType.NetworkError -> assertTrue(true)
                is ErrorType.ServerError -> assertTrue(true)
                is ErrorType.DataFormatError -> assertTrue(true)
                is ErrorType.ValidationError -> assertTrue(error.details.isNotEmpty())
                is ErrorType.UnexpectedError -> assertTrue(error.details.isNotEmpty())
            }
        }
    }

    @Test
    fun `different error types are not equal`() {
        // Then
        assertNotEquals(ErrorType.NetworkError, ErrorType.ServerError)
        assertNotEquals(ErrorType.ServerError, ErrorType.DataFormatError)
        assertNotEquals(ErrorType.NetworkError, ErrorType.ValidationError("test"))
    }
}
