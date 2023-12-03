package com.example.api.dto

import java.util.*

class ReserveConfirmation {
    data class Request(
        val restKey: UUID,   // 키오스크가 줄 정보
        val reserveKey: UUID,   // 유저가 줄 정보
        val userId: String,   // 유저가 줄 정보
    )
}