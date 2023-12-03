package com.example.api.exception

import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

class ErrorResponse(
    private val customException: CustomException
) {
    fun toResponseEntity(): ResponseEntity<ErrorResponseDto> {
        val errorCode = customException.customErrorCode

        return ResponseEntity.status(errorCode.statusCode)
            .body(
                ErrorResponseDto(
                    errorCode = errorCode.errorCode,
                    errorMessage = errorCode.errorMessage
                )
            )
    }

    data class ErrorResponseDto(
        val errorCode: String,
        val errorMessage: String
    ) {
        val timeStamp = LocalDateTime.now()
    }
}