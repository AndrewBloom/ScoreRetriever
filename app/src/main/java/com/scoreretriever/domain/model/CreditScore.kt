package com.scoreretriever.domain.model

/**
 * Domain model representing a user's credit score.
 *
 * This is a pure Kotlin data class with no Android or framework dependencies,
 * making it easy to test and maintain.
 *
 * @property score The current credit score value
 * @property maxScore The maximum possible credit score value
 */
data class CreditScore(
    val score: Int,
    val maxScore: Int
) {
    /**
     * Calculates the percentage of the current score relative to the maximum score.
     * Returns a value between 0.0 and 1.0
     */
    val percentage: Float
        get() = if (maxScore > 0) score.toFloat() / maxScore.toFloat() else 0f

    /**
     * Validates that the score is within valid bounds
     */
    fun isValid(): Boolean {
        return score >= 0 && maxScore > 0 && score <= maxScore
    }

    init {
        require(maxScore > 0) { "Max score must be greater than 0" }
        require(score >= 0) { "Score must be non-negative" }
        require(score <= maxScore) { "Score cannot exceed max score" }
    }
}
