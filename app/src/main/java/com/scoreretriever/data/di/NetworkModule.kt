package com.scoreretriever.data.di

import com.scoreretriever.data.api.CreditScoreApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module for providing network-related dependencies.
 *
 * This module follows Dependency Inversion Principle:
 * - Provides concrete implementations of network components
 * - Configured as Singleton to ensure single instances
 * - InstallIn(SingletonComponent) makes dependencies available app-wide
 *
 * All provided dependencies are singletons to avoid creating multiple instances
 * of expensive objects like OkHttpClient and Retrofit.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides HttpLoggingInterceptor for debugging network requests.
     *
     * Logs request and response details to help with debugging.
     * In production, this should be removed or set to NONE level.
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Provides OkHttpClient with logging interceptor and timeout configuration.
     *
     * Configures:
     * - Connection timeout: 30 seconds
     * - Read timeout: 30 seconds
     * - Write timeout: 30 seconds
     * - Logging interceptor for debugging
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provides Retrofit instance configured with:
     * - Base URL for the credit score API
     * - Gson converter for JSON serialization/deserialization
     * - OkHttp client with interceptors
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CreditScoreApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides CreditScoreApi interface implementation.
     *
     * Retrofit generates the implementation at runtime.
     */
    @Provides
    @Singleton
    fun provideCreditScoreApi(retrofit: Retrofit): CreditScoreApi {
        return retrofit.create(CreditScoreApi::class.java)
    }
}
