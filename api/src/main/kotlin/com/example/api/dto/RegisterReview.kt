package com.example.api.dto

import java.util.*

class RegisterReview {
    data class Request(
        val reserveKey: UUID,
        val star: Int,
        val comment: String
    )
}