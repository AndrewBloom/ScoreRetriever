package com.scoreretriever.data.api.dto

import com.scoreretriever.domain.model.CreditScore
import com.google.gson.annotations.SerializedName

/**
 * Root DTO for the credit score API response.
 *
 * This represents the top-level JSON object returned by the endpoint.
 * Only the fields needed for our use case are included (creditReportInfo).
 * Additional fields from the API can be added here if needed in the future.
 */
data class CreditScoreResponseDto(
    @SerializedName("creditReportInfo")
    val creditReportInfo: CreditReportInfoDto,

    @SerializedName("accountIDVStatus")
    val accountIDVStatus: String? = null,

    @SerializedName("dashboardStatus")
    val dashboardStatus: String? = null,

    @SerializedName("personaType")
    val personaType: String? = null

    // TODO Andrea 18/10
    // coachingSummary and augmentedCreditScore currently not fetched

) {
    /**
     * Converts this DTO to a domain model.
     *
     * This mapping function keeps the data layer decoupled from the domain layer.
     * It follows the Dependency Rule: data layer depends on domain, but not vice versa.
     *
     * @return CreditScore domain model
     */
    fun toDomainModel(): CreditScore {
        return CreditScore(
            score = creditReportInfo.score,
            maxScore = creditReportInfo.maxScoreValue
        )
    }
}
