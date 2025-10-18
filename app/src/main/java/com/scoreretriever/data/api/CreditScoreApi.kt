package com.scoreretriever.data.api

import com.scoreretriever.data.api.dto.CreditScoreResponseDto
import retrofit2.http.GET

/**
 * Retrofit API interface for credit score endpoints.
 *
 * This interface follows the Single Responsibility Principle:
 * - Only responsible for defining API endpoints
 * - No business logic or error handling
 * - Uses suspend functions for Kotlin coroutines
 *
 * Retrofit will generate the implementation at runtime.
 */
interface CreditScoreApi {
    /**
     * Fetches credit score information from the endpoint.
     *
     * Uses suspend function for coroutine support.
     * Throws exceptions on network or HTTP errors (handled by repository).
     *
     * @return CreditScoreResponseDto containing credit score data
     */
    @GET("endpoint.json")
    suspend fun getCreditScore(): CreditScoreResponseDto

    companion object {
        const val BASE_URL = "https://android-interview.s3.eu-west-2.amazonaws.com/"
    }
}
