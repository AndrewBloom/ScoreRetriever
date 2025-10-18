package com.scoreretriever.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for credit report information from the API.
 *
 * This class maps directly to the JSON structure returned by the API.
 * It uses @SerializedName annotations for Gson serialization/deserialization.
 *
 * DTOs are kept in the data layer and converted to domain models before being
 * passed to the domain/presentation layers (Dependency Rule of Clean Architecture).
 */
data class CreditReportInfoDto(
    @SerializedName("score")
    val score: Int,

    @SerializedName("maxScoreValue")
    val maxScoreValue: Int,

    @SerializedName("scoreBand")
    val scoreBand: Int? = null,

    @SerializedName("clientRef")
    val clientRef: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("creditReportStatus")
    val creditReportStatus: String? = null,

    @SerializedName("daysUntilNextReport")
    val daysUntilNextReport: Int? = null,

    @SerializedName("hasEverBeenScore")
    val hasEverBeenScore: Boolean? = null,

    @SerializedName("monthsSinceLastDefaulted")
    val monthsSinceLastDefaulted: Int? = null,

    @SerializedName("hasEverDefaulted")
    val hasEverDefaulted: Boolean? = null,

    @SerializedName("monthsSinceLastDelinquent")
    val monthsSinceLastDelinquent: Int? = null,

    @SerializedName("hasEverBeenDelinquent")
    val hasEverBeenDelinquent: Boolean? = null,

    @SerializedName("percentageCreditUsed")
    val percentageCreditUsed: Int? = null,

    @SerializedName("percentageCreditUsedDirectionFlag")
    val percentageCreditUsedDirectionFlag: Int? = null,

    @SerializedName("changedScore")
    val changedScore: Int? = null,

    @SerializedName("currentShortTermDebt")
    val currentShortTermDebt: Int? = null,

    @SerializedName("currentShortTermNonPromotionalDebt")
    val currentShortTermNonPromotionalDebt: Int? = null,

    @SerializedName("currentShortTermCreditLimit")
    val currentShortTermCreditLimit: Int? = null,

    @SerializedName("currentShortTermCreditUtilisation")
    val currentShortTermCreditUtilisation: Int? = null,

    @SerializedName("changeInShortTermDebt")
    val changeInShortTermDebt: Int? = null,

    @SerializedName("currentLongTermDebt")
    val currentLongTermDebt: Int? = null,

    @SerializedName("currentLongTermNonPromotionalDebt")
    val currentLongTermNonPromotionalDebt: Int? = null,

    @SerializedName("currentLongTermCreditLimit")
    val currentLongTermCreditLimit: Int? = null,

    @SerializedName("currentLongTermCreditUtilisation")
    val currentLongTermCreditUtilisation: Int? = null,

    @SerializedName("changeInLongTermDebt")
    val changeInLongTermDebt: Int? = null,

    @SerializedName("numPositiveScoreFactors")
    val numPositiveScoreFactors: Int? = null,

    @SerializedName("numNegativeScoreFactors")
    val numNegativeScoreFactors: Int? = null,

    @SerializedName("equifaxScoreBand")
    val equifaxScoreBand: Int? = null,

    @SerializedName("equifaxScoreBandDescription")
    val equifaxScoreBandDescription: String? = null
)
