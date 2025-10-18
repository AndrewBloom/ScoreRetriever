package com.scoreretriever.domain.model

/**
 * Sealed class representing different types of errors that can occur in the application.
 *
 * This follows Clean Architecture principles:
 * - Domain layer defines error types without Android dependencies
 * - Presentation layer maps error types to localized user-facing messages
 * - Keeps error handling logic separate from UI concerns
 *
 * Each error type represents a specific category of failure that may require
 * different handling or user messaging.
 */
sealed class ErrorType {
    /**
     * Network connectivity error (no internet, timeout, DNS failure, etc.)
     */
    data object NetworkError : ErrorType()

    /**
     * Server-side error (HTTP 4xx, 5xx responses)
     */
    data object ServerError : ErrorType()

    /**
     * Data parsing/format error (invalid JSON, unexpected structure)
     */
    data object DataFormatError : ErrorType()

    /**
     * Domain validation error (business rule violation)
     * @property details Specific validation error details
     */
    data class ValidationError(val details: String) : ErrorType()

    /**
     * Unexpected error (catch-all for unknown errors)
     * @property details Error details for logging/debugging
     */
    data class UnexpectedError(val details: String) : ErrorType()
}
