package com.example.api.dto

import com.example.database.domain.RestaurantInfo
import java.util.*

data class RestInfoDto(
    val restKey: UUID,
    val name: String,
    val address: String,
    val description: String,
    val ownerId: String,
    val averageStar: Double
) {
    companion object {
        fun fromEntity(restInfo: RestaurantInfo): RestInfoDto {
            return RestInfoDto(
                name = restInfo.name,
                address = restInfo.address,
                description = restInfo.description,
                ownerId = restInfo.ownerId,
                restKey = restInfo.restKey!!,
                averageStar = restInfo.averageStar
            )
        }
    }
}

