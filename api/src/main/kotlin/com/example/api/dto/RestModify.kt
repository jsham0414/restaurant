package com.example.api.dto

class RestModify {
    data class Request(
        val name: String?,
        val address: String?,
        val description: String?,
        val ownerId: String?,
        val phone: String?
    )
}