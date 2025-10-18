package com.scoreretriever.data.repository

import com.scoreretriever.data.api.CreditScoreApi
import com.scoreretriever.domain.model.CreditScore
import com.scoreretriever.domain.model.ErrorType
import com.scoreretriever.domain.model.Result
import com.scoreretriever.domain.repository.CreditScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of CreditScoreRepository interface.
 *
 * This class follows Clean Architecture principles:
 * - Implements interface defined in domain layer
 * - Handles data layer concerns (API calls, error handling, mapping)
 * - Converts DTOs to domain models
 * - Wraps results in Result type for explicit error handling
 *
 * Uses constructor injection with @Inject for Hilt dependency injection.
 *
 * @property api The Retrofit API interface (injected by Hilt)
 */
class CreditScoreRepositoryImpl @Inject constructor(
    private val api: CreditScoreApi
) : CreditScoreRepository {

    /**
     * Fetches credit score from the API.
     *
     * This method:
     * 1. Makes the API call using coroutines
     * 2. Converts DTO to domain model
     * 3. Wraps result in Result.Success
     * 4. Catches exceptions and wraps in Result.Error
     * 5. Returns Flow for reactive streams
     *
     * Error handling covers:
     * - Network errors (IOException)
     * - HTTP errors (HttpException)
     * - Parsing errors (JsonSyntaxException)
     * - Validation errors (IllegalArgumentException from domain model)
     * - Any other unexpected errors
     *
     * @return Flow emitting Result containing CreditScore or Error
     */
    override fun getCreditScore(): Flow<Result<CreditScore>> = flow {
        try {
            // Make API call
            val response = api.getCreditScore()

            // Convert DTO to domain model
            val creditScore = response.toDomainModel()

            // Emit success result
            emit(Result.Success(creditScore))
        } catch (e: IOException) {
            // Network error
            emit(Result.Error(
                errorType = ErrorType.NetworkError,
                exception = e
            ))
        } catch (e: retrofit2.HttpException) {
            // HTTP error (4xx, 5xx)
            emit(Result.Error(
                errorType = ErrorType.ServerError,
                exception = e
            ))
        } catch (e: com.google.gson.JsonSyntaxException) {
            // JSON parsing error
            emit(Result.Error(
                errorType = ErrorType.DataFormatError,
                exception = e
            ))
        } catch (e: IllegalArgumentException) {
            // Domain model validation error
            emit(Result.Error(
                errorType = ErrorType.ValidationError(e.message ?: "Unknown validation error"),
                exception = e
            ))
        } catch (e: Exception) {
            // Any other unexpected error
            emit(Result.Error(
                errorType = ErrorType.UnexpectedError(e.message ?: "Unknown error"),
                exception = e
            ))
        }
    }
}
