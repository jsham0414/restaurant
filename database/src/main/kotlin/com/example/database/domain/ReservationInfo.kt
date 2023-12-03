package com.example.database.domain

import com.example.database.model.constant.ReservationStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "RESERVATION")
class ReservationInfo(
    // 예약 시간
    @Column(name = "date")
    val date: LocalDateTime,

    // 매장 키
    @Column(name = "rest_key")
    val restKey: UUID,

    // 유저 아이디
    @Column(name = "user_id")
    val userId: String,

    // 예약 상태
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus
) {
    @Id
    @Column(name = "reserve_key")
    @GeneratedValue(strategy = GenerationType.UUID)
    val reserveKey: UUID? = null


}