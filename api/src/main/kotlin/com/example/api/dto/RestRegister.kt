package com.example.api.dto

import com.example.database.domain.RestaurantInfo
import java.util.*

class RestRegister {
    data class Request(
        val name: String,
        val address: String,
        val description: String,
        val phone: String
    ) {
        fun toEntity(ownerId: String): RestaurantInfo {
            return RestaurantInfo(
                name = name,
                address = address,
                description = description,
                ownerId = ownerId,
                phone = phone
            )
        }
    }

    data class Response(
        val restKey: UUID
    )
}