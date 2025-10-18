package com.scoreretriever.domain.usecase

import com.scoreretriever.domain.model.CreditScore
import com.scoreretriever.domain.model.Result
import com.scoreretriever.domain.repository.CreditScoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving credit score information.
 *
 * This class follows the Single Responsibility Principle:
 * - Its only job is to coordinate credit score retrieval
 * - Business logic can be added here (e.g., caching, validation)
 * - Isolates the ViewModel from direct repository access
 *
 * Uses constructor injection with @Inject for Hilt dependency injection.
 *
 * @property repository The credit score repository (injected by Hilt)
 */
class GetCreditScoreUseCase @Inject constructor(
    private val repository: CreditScoreRepository
) {
    /**
     * Executes the use case to fetch credit score.
     *
     * This is a simple pass-through for now, but in a real application,
     * this is where we would add:
     * - Business rules and validation
     * - Data transformation
     * - Caching logic
     * - Analytics tracking
     * - Error recovery strategies
     *
     * @return Flow emitting Result containing CreditScore or Error
     */
    operator fun invoke(): Flow<Result<CreditScore>> {
        return repository.getCreditScore()
    }
}
