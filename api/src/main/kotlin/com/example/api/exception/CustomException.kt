package com.example.api.exception

class CustomException(val customErrorCode: ErrorCode) : RuntimeException() {
    override fun toString(): String {
        return "" + customErrorCode.statusCode +
                " " + customErrorCode.errorCode +
                " " + customErrorCode.errorMessage
    }
}