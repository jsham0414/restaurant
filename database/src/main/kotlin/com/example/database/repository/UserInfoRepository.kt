package com.example.database.repository

import com.example.database.domain.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserInfoRepository : JpaRepository<UserInfo, String> {
    fun existsByUserId(userId: String): Boolean
    fun findByUserId(userId: String): UserInfo?

    @Query("SELECT u.phone FROM UserInfo u WHERE u.userId = %:userId%")
    fun findPhoneByUserId(userId: String): String?
}