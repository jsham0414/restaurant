package com.example.api.dto

import com.example.database.domain.UserInfo

data class UserInfoDto(
    val userId: String,
    val userPw: String,
    val roles: String,
    var phone: String
) {
    fun fromEntity(userInfo: UserInfo): UserInfoDto {
        return UserInfoDto(
            userId = userId,
            userPw = userPw,
            roles = roles,
            phone = phone
        )
    }
}