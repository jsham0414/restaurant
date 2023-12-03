package com.example.database.repository

import com.example.database.domain.ReviewInfo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReviewInfoRepository : JpaRepository<ReviewInfo, UUID> {
    fun findByReviewKey(reviewKey: UUID): ReviewInfo?
    fun existsByReserveKey(reserveKey: UUID): Boolean
    fun findByReserveKey(reserveKey: UUID): ReviewInfo?
}