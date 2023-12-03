package com.example.api.com.example.api.exception

import com.example.api.exception.CustomException
import com.example.api.exception.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(CustomException::class)
    fun customExceptionHandler(e: CustomException) =
        ErrorResponse(e).toResponseEntity()
}