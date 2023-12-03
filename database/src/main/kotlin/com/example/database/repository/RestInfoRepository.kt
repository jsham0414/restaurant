package com.example.database.repository

import com.example.database.domain.RestaurantInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RestInfoRepository : JpaRepository<RestaurantInfo, UUID> {
    fun findByNameLike(name: String): List<RestaurantInfo>
    fun findByAddressLike(address: String): List<RestaurantInfo>

    fun findByNameContainsOrderByAverageStarDesc(name: String): List<RestaurantInfo>
    fun findByAddressContainsOrderByAverageStarDesc(address: String): List<RestaurantInfo>

    @Query("SELECT r.phone FROM RestaurantInfo r WHERE r.restKey = %:restKey%")
    fun findPhoneByRestKey(restKey: UUID): String?

    fun existsByRestKey(restKey: UUID): Boolean
    fun findByRestKey(restKey: UUID): RestaurantInfo?

    fun findByOwnerId(userId: String): List<RestaurantInfo>
}