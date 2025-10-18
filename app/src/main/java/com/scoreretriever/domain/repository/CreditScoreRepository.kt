package com.scoreretriever.domain.repository

import com.scoreretriever.domain.model.CreditScore
import com.scoreretriever.domain.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for credit score data access.
 *
 * This interface follows the Repository pattern and Dependency Inversion Principle:
 * - Defined in the domain layer (not data layer)
 * - Depends on domain models, not DTOs
 * - Returns Flow for reactive data streams
 * - Wraps results in Result type for explicit error handling
 *
 * The implementation will be provided by the data layer and injected via Hilt.
 */
interface CreditScoreRepository {
    /**
     * Fetches the user's credit score from the remote data source.
     *
     * @return Flow emitting Result containing CreditScore or Error
     */
    fun getCreditScore(): Flow<Result<CreditScore>>
}
