package com.scoreretriever.domain.model

/**
 * A generic sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with an error message and optional exception.
 *
 * This follows the Result pattern for clean error handling and explicit success/failure states.
 */
sealed class Result<out T> {
    /**
     * Represents a successful result with data of type [T]
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents a failure with an error type and optional exception
     *
     * @property errorType The type of error that occurred
     * @property exception Optional exception for debugging/logging
     */
    data class Error(
        val errorType: ErrorType,
        val exception: Throwable? = null
    ) : Result<Nothing>()

    /**
     * Returns true if this is a Success result
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Returns true if this is an Error result
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Returns the data if Success, null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
}
