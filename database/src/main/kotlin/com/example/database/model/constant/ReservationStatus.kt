package com.example.database.model.constant

enum class ReservationStatus {
    PENDING,    // 승인 대기 중
    REFUSED,   // 예약 거절됨
    CANCELED,   // 예약 취소됨
    APPROVED,   // 예약 승인됨
    CONFIRMED   // 도착 확인됨
}