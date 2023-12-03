package com.example.api.dto

import com.example.database.domain.ReviewInfo
import java.util.*

data class ReviewInfoDto(
    val reviewKey: UUID,
    val reserveKey: UUID,
    val star: Int,
    val comment: String
) {
    companion object {
        fun fromEntity(reviewInfo: ReviewInfo): ReviewInfoDto {
            return ReviewInfoDto(
                reviewKey = reviewInfo.reviewKey!!,
                reserveKey = reviewInfo.reserveKey,
                star = reviewInfo.star,
                comment = reviewInfo.comment
            )
        }
    }

    fun toEntity(): ReviewInfo {
        return ReviewInfo(
            reserveKey = reserveKey,
            star = star,
            comment = comment
        )
    }
}