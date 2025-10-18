package com.scoreretriever.data.api.dto

import com.scoreretriever.domain.model.Score
import com.google.gson.annotations.SerializedName

/**
 * Root DTO for the score API response.
 *
 * This represents the top-level JSON object returned by the endpoint.
 * Only the fields needed for our use case are included (creditReportInfo).
 * Additional fields from the API can be added here if needed in the future.
 */
data class ScoreResponseDto(
    @SerializedName("creditReportInfo")
    val scoreInfo: ScoreInfoDto,

    @SerializedName("accountIDVStatus")
    val accountIDVStatus: String? = null,

    @SerializedName("dashboardStatus")
    val dashboardStatus: String? = null,

    @SerializedName("personaType")
    val personaType: String? = null

    // TODO Andrea 18/10
    // coachingSummary and augmentedScore currently not fetched

) {
    /**
     * Converts this DTO to a domain model.
     *
     * This mapping function keeps the data layer decoupled from the domain layer.
     * It follows the Dependency Rule: data layer depends on domain, but not vice versa.
     *
     * @return Score domain model
     */
    fun toDomainModel(): Score {
        return Score(
            score = scoreInfo.score,
            maxScore = scoreInfo.maxScoreValue
        )
    }
}
