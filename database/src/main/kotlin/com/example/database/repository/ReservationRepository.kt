package com.example.database.repository

import com.example.database.domain.ReservationInfo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReservationRepository : JpaRepository<ReservationInfo, UUID> {
    fun findByRestKeyOrderByDate(restKey: UUID): List<ReservationInfo>
    fun findByReserveKey(reserveKey: UUID): ReservationInfo?

    fun findByUserId(userId: String): List<ReservationInfo>
}