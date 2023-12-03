package com.example.api.dto

import com.example.database.domain.UserInfo

class Auth {
    class Request {
        data class SignIn(
            val userId: String,
            val userPw: String
        )

        data class SignUp(
            val userId: String,
            val userPw: String,
            val phone: String
        ) {
            fun toEntity(roles: String): UserInfo {
                return UserInfo(
                    userId = userId,
                    userPw = userPw,
                    roles = roles,
                    phone = phone
                )
            }
        }

    }

    class Response {
        data class SignIn(
            val token: String
        )

        data class SignUp(
            val userId: String,
            val userPw: String,
            val roles: String,
            val phone: String
        )
    }

}