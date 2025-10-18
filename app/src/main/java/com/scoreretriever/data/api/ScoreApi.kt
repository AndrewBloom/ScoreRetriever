package com.scoreretriever.data.api

import com.scoreretriever.data.api.dto.ScoreResponseDto
import retrofit2.http.GET

/**
 * Retrofit API interface for score endpoints.
 *
 * This interface follows the Single Responsibility Principle:
 * - Only responsible for defining API endpoints
 * - No business logic or error handling
 * - Uses suspend functions for Kotlin coroutines
 *
 * Retrofit will generate the implementation at runtime.
 */
interface ScoreApi {
    /**
     * Fetches score information from the endpoint.
     *
     * Uses suspend function for coroutine support.
     * Throws exceptions on network or HTTP errors (handled by repository).
     *
     * @return ScoreResponseDto containing score data
     */
    @GET("endpoint.json")
    suspend fun getScore(): ScoreResponseDto

    companion object {
        const val BASE_URL = "https://android-interview.s3.eu-west-2.amazonaws.com/"
    }
}
