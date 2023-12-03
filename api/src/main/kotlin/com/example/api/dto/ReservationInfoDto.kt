package com.example.api.dto

import com.example.database.domain.ReservationInfo
import com.example.database.model.constant.ReservationStatus
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.*

data class ReservationInfoDto(
    val reserveKey: UUID,
    val userId: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-ddTHH:mm:ss")
    @Parameter(description = "날짜 형식 : yyyy-MM-ddTHH:mm:ss")
    val date: LocalDateTime,
    val restKey: UUID,
    var status: ReservationStatus,
    val phone: String
) {
    companion object {
        fun fromEntity(reservationInfo: ReservationInfo, phone: String): ReservationInfoDto {
            return ReservationInfoDto(
                reserveKey = reservationInfo.reserveKey!!,
                date = reservationInfo.date,
                restKey = reservationInfo.restKey,
                userId = reservationInfo.userId,
                status = reservationInfo.status,
                phone = phone
            )
        }
    }
}