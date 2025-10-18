package com.scoreretriever.domain.repository

import com.scoreretriever.domain.model.Score
import com.scoreretriever.domain.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for score data access.
 *
 * This interface follows the Repository pattern and Dependency Inversion Principle:
 * - Defined in the domain layer (not data layer)
 * - Depends on domain models, not DTOs
 * - Returns Flow for reactive data streams
 * - Wraps results in Result type for explicit error handling
 *
 * The implementation will be provided by the data layer and injected via Hilt.
 */
interface ScoreRepository {
    /**
     * Fetches the user's score from the remote data source.
     *
     * @return Flow emitting Result containing Score or Error
     */
    fun getScore(): Flow<Result<Score>>
}
