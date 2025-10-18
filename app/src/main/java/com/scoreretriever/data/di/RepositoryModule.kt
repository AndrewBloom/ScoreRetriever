package com.scoreretriever.data.di

import com.scoreretriever.data.repository.CreditScoreRepositoryImpl
import com.scoreretriever.domain.repository.CreditScoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository interfaces to implementations.
 *
 * This module demonstrates Dependency Inversion Principle:
 * - Domain layer defines the interface (CreditScoreRepository)
 * - Data layer provides the implementation (CreditScoreRepositoryImpl)
 * - This module binds them together for dependency injection
 *
 * Uses @Binds instead of @Provides for better performance:
 * - @Binds is used for simple interface-to-implementation mapping
 * - @Provides is used when more complex instantiation logic is needed
 *
 * The repository is provided as a Singleton to maintain a single instance
 * throughout the app lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds CreditScoreRepository interface to CreditScoreRepositoryImpl.
     *
     * When a class requests CreditScoreRepository via constructor injection,
     * Hilt will provide an instance of CreditScoreRepositoryImpl.
     *
     * This allows us to:
     * - Easily swap implementations (e.g., for testing or different data sources)
     * - Keep domain layer independent of data layer implementation details
     * - Follow Interface Segregation and Dependency Inversion principles
     */
    @Binds
    @Singleton
    abstract fun bindCreditScoreRepository(
        impl: CreditScoreRepositoryImpl
    ): CreditScoreRepository
}
