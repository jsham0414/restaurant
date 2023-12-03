package com.example.api.com.example.api.controller

import com.example.api.dto.MakeReservation
import com.example.api.dto.ReserveConfirmation
import com.example.api.service.ReservationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/restaurant/api/v1/reservation")
@Tag(name = "예약 컨트롤러", description = "예약에 관한 다양한 엔드포인트를 제공합니다.")
class ReservationController(
    private val reservationService: ReservationService
) {
    // 예약 만들기 (현재 시간 + 10분 부터 예약 가능)
    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'PARTNER')")
    @Operation(summary = "예약 등록", description = "날짜를 받아 예약을 등록합니다. 너무 이른 예약은 거절됩니다.")
    fun make(
        @RequestHeader header: HttpHeaders,
        @RequestBody request: MakeReservation.Request
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(reservationService.makeReservation(header, request))
    }

    // 키오스트에서 사용할 요청
    @PostMapping("/confirmation")
    @Operation(summary = "방문 확인", description = "예약자의 인증 후 유효한 예약인지 확인합니다.")
    fun confirmation(@RequestBody request: ReserveConfirmation.Request) {
        reservationService.reservationConfirmation(request)
    }

    // 예약 승인 (해당 매장의 관리자만 가능)
    @PutMapping("/approve/{reserveKey}")
    @PreAuthorize("hasRole('PARTNER')")
    @Operation(summary = "예약 승인", description = "관리자 인증 후 매장에 신청된 예약을 승인합니다.")
    fun approve(@RequestHeader header: HttpHeaders, @PathVariable reserveKey: UUID) {
        reservationService.approve(header, reserveKey)
    }

    // 예약 거절 (해당 매장의 관리자만 가능)
    @PutMapping("/refuse/{reserveKey}")
    @PreAuthorize("hasRole('PARTNER')")
    @Operation(summary = "예약 거절", description = "관리자 인증 후 매장에 신청된 예약을 거절합니다.")
    fun refuse(@RequestHeader header: HttpHeaders, @PathVariable reserveKey: UUID) {
        reservationService.refuse(header, reserveKey)
    }

    // 예약 취소 (해당 예약을 신청한 맴버만 가능)
    @PutMapping("/cancel/{reserveKey}")
    @PreAuthorize("hasAnyRole('MEMBER', 'PARTNER')")
    @Operation(summary = "예약 취소", description = "예약자의 인증 후 본인의 예약을 취소합니다.")
    fun cancel(@RequestHeader header: HttpHeaders, @PathVariable reserveKey: UUID) {
        reservationService.cancel(header, reserveKey)
    }

    // 고객용 예약 리스트
    @GetMapping("/customer/{userId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'PARTNER')")
    @Operation(summary = "고객용 예약 리스트 조회", description = "고객이 신청했던 모든 예약을 조회합니다.")
    fun getReservationsForCustomer(@RequestHeader header: HttpHeaders)
            : ResponseEntity<Any> {
        return ResponseEntity.ok(reservationService.getCustomerReservations(header))
    }

    // 관리자용 예약 리스트
    @GetMapping("/partner/{restKey}")
    @PreAuthorize("hasRole('PARTNER')")
    @Operation(summary = "관리자용 예약 리스트 조회", description = "매장에 신청되었던 모든 예약을 조회합니다.")
    fun getReservationsForOwner(@RequestHeader header: HttpHeaders, @PathVariable restKey: UUID)
            : ResponseEntity<Any> {
        return ResponseEntity.ok(reservationService.getReservationList(header, restKey))
    }
}