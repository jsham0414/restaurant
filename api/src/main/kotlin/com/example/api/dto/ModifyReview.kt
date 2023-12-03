package com.example.api.dto

import java.util.*

class ModifyReview {
    data class Request(
        val reviewKey: UUID,
        val star: Int?,
        val comment: String?
    )
}