package com.scoreretriever.domain.usecase

import com.scoreretriever.domain.model.Score
import com.scoreretriever.domain.model.Result
import com.scoreretriever.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving score information.
 *
 * This class follows the Single Responsibility Principle:
 * - Its only job is to coordinate score retrieval
 * - Business logic can be added here (e.g., caching, validation)
 * - Isolates the ViewModel from direct repository access
 *
 * Uses constructor injection with @Inject for Hilt dependency injection.
 *
 * @property repository The score repository (injected by Hilt)
 */
class GetScoreUseCase @Inject constructor(
    private val repository: ScoreRepository
) {
    /**
     * Executes the use case to fetch score.
     *
     * This is a simple pass-through for now, but in a real application,
     * this is where we would add:
     * - Business rules and validation
     * - Data transformation
     * - Caching logic
     * - Analytics tracking
     * - Error recovery strategies
     *
     * @return Flow emitting Result containing Score or Error
     */
    operator fun invoke(): Flow<Result<Score>> {
        return repository.getScore()
    }
}
