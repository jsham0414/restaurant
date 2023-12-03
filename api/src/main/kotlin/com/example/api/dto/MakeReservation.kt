package com.example.api.dto

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.*

class MakeReservation {
    data class Request(
        val restKey: UUID,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-ddTHH:mm:ss")
        @Parameter(description = "날짜 형식 : yyyy-MM-ddTHH:mm:ss")
        val date: LocalDateTime
    )
}