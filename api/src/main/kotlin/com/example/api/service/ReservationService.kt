package com.example.api.service

import com.example.api.dto.MakeReservation
import com.example.api.dto.ReservationInfoDto
import com.example.api.dto.ReserveConfirmation
import com.example.api.exception.CustomException
import com.example.api.exception.ErrorCode
import com.example.api.security.TokenProvider
import com.example.database.domain.ReservationInfo
import com.example.database.model.constant.ReservationStatus
import com.example.database.repository.ReservationRepository
import com.example.database.repository.RestInfoRepository
import com.example.database.repository.UserInfoRepository
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val userInfoRepository: UserInfoRepository,
    private val restInfoRepository: RestInfoRepository,
    private val tokenProvider: TokenProvider
) {
    // 예약 진행 (매장 키, 시간) : 예약 정보
    @Transactional
    fun makeReservation(header: HttpHeaders, request: MakeReservation.Request)
            : ReservationInfoDto {
        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 유저 아이디가 실제로 존재하는지 확인
        if (!userInfoRepository.existsByUserId(userId)) {
            throw CustomException(ErrorCode.INVALID_USER_ID)
        }

        if (!restInfoRepository.existsByRestKey(request.restKey)) {
            throw CustomException(ErrorCode.INVALID_REST_KEY)
        }

        // 현재 시각 + 10분 보다 느린 예약이면 예외 발생
        val nowDate = LocalDateTime.now()
        if (nowDate.plusMinutes(10).isAfter(request.date)) {
            throw CustomException(ErrorCode.EARLY_REQUEST)
        }

        val reservationInfo = ReservationInfo(
            date = request.date,
            restKey = request.restKey,
            userId = userId,
            status = ReservationStatus.PENDING
        )

        val phone = userInfoRepository.findPhoneByUserId(userId)!!

        reservationRepository.save(reservationInfo)

        return ReservationInfoDto.fromEntity(reservationInfo, phone)
    }

    // 도착 확인
    @Transactional
    fun reservationConfirmation(request: ReserveConfirmation.Request) {
        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(request.restKey)
            ?: throw CustomException(ErrorCode.INVALID_REST_KEY)

        if (!userInfoRepository.existsByUserId(request.userId)) {
            throw CustomException(ErrorCode.USER_NOT_FOUNDED)
        }

        val reserveInfo = reservationRepository.findByReserveKey(request.reserveKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        reservationTimeCheck(reserveInfo)
        reservationCheck(reserveInfo)

        // 상태를 도착 확인 완료로 전환
        reserveInfo.status = ReservationStatus.CONFIRMED

        reservationRepository.save(reserveInfo)
    }

    fun reservationTimeCheck(reserveInfo: ReservationInfo) {
        val nowDate = LocalDateTime.now()

        // 일찍 도착 확인을 한 경우
        if (nowDate.isBefore(reserveInfo.date.minusMinutes(10))) {
            throw CustomException(ErrorCode.TIME_IS_NOT_UP)
        }

        // 예약자가 늦게 온 경우
        if (nowDate.isAfter(reserveInfo.date)) {
            throw CustomException(ErrorCode.TIME_HAS_ENDED)
        }
    }

    fun reservationCheck(reserveInfo: ReservationInfo) {
        // 본인이 취소한 예약일 때
        if (reserveInfo.status == ReservationStatus.CANCELED) {
            throw CustomException(ErrorCode.CANCELED_RESERVATION)
        }

        // 매장 측에서 거절한 예약일 때
        if (reserveInfo.status == ReservationStatus.REFUSED) {
            throw CustomException(ErrorCode.REFUSED_RESERVATION)
        }

        // 예약 확인 중에 있는 예약일 때
        if (reserveInfo.status == ReservationStatus.PENDING) {
            throw CustomException(ErrorCode.NOT_APPROVAL_RESERVATION)
        }

        if (reserveInfo.status != ReservationStatus.APPROVED) {
            throw CustomException(ErrorCode.UNEXPECTED_ERROR)
        }
    }

    // 예약 승인
    fun approve(header: HttpHeaders, reserveKey: UUID) {
        val userKey = tokenProvider.resolveTokenFromHeader(header)

        val reserveInfo = reservationRepository.findByReserveKey(reserveKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(reserveInfo.restKey)
            ?: throw CustomException(ErrorCode.INVALID_REST_KEY)

        // 가게 사장이 아닐 경우
        if (restInfo.ownerId != userKey) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        // 대기 중인 예약이 아닐 경우
        if (reserveInfo.status != ReservationStatus.PENDING) {
            throw CustomException(ErrorCode.CANT_MODIFY_STATUS)
        }

        if (reserveInfo.status == ReservationStatus.APPROVED) {
            throw CustomException(ErrorCode.CANT_MODIFY_STATUS)
        }

        reserveInfo.status = ReservationStatus.APPROVED

        reservationRepository.save(reserveInfo)
    }

    // 예약 거절
    fun refuse(header: HttpHeaders, reserveKey: UUID) {
        val userKey = tokenProvider.resolveTokenFromHeader(header)

        val reserveInfo = reservationRepository.findByReserveKey(reserveKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(reserveInfo.restKey)
            ?: throw CustomException(ErrorCode.INVALID_REST_KEY)

        // 가게 사장이 아닐 경우
        if (restInfo.ownerId != userKey) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        if (reserveInfo.status == ReservationStatus.REFUSED) {
            throw CustomException(ErrorCode.CANT_MODIFY_STATUS)
        }

        // 대기 중인 예약이 아닐 경우
        if (reserveInfo.status != ReservationStatus.PENDING) {
            throw CustomException(ErrorCode.CANT_MODIFY_STATUS)
        }

        reserveInfo.status = ReservationStatus.REFUSED

        reservationRepository.save(reserveInfo)
    }

    // 예약 취소
    fun cancel(header: HttpHeaders, reserveKey: UUID) {
        val userId = tokenProvider.resolveTokenFromHeader(header)

        val reserveInfo = reservationRepository.findByReserveKey(reserveKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(reserveInfo.restKey)
            ?: throw CustomException(ErrorCode.INVALID_REST_KEY)

        // 예약한 본인이 아닐 경우
        if (reserveInfo.userId != userId) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        if (reserveInfo.status == ReservationStatus.CANCELED) {
            throw CustomException(ErrorCode.ALREADY_CANCELED)
        }

        if (reserveInfo.status == ReservationStatus.REFUSED) {
            throw CustomException(ErrorCode.ALREADY_REFUSED)
        }

        if (reserveInfo.status == ReservationStatus.CONFIRMED) {
            throw CustomException(ErrorCode.ALREADY_CONFIRMED)
        }

        reserveInfo.status = ReservationStatus.CANCELED

        reservationRepository.save(reserveInfo)
    }

    // 매장 번호를 포함한 고객용 예약 리스트 반환
    fun getCustomerReservations(header: HttpHeaders)
            : List<ReservationInfoDto> {
        val userId = tokenProvider.resolveTokenFromHeader(header)

        if (!userInfoRepository.existsByUserId(userId))
            throw CustomException(ErrorCode.INVALID_USER_ID)

        return reservationRepository.findByUserId(userId).stream().map {
            // 매장 전화 번호
            val phone = restInfoRepository.findPhoneByRestKey(it.restKey)!!

            ReservationInfoDto.fromEntity(it, phone)
        }.toList()
    }

    // 고객 전화 번호를 포함한 예약 리스트 반환
    fun getReservationList(header: HttpHeaders, restKey: UUID)
            : List<ReservationInfoDto> {
        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(restKey)
            ?: throw CustomException(ErrorCode.INVALID_REST_KEY)

        val userId = tokenProvider.resolveTokenFromHeader(header)
        if (!userInfoRepository.existsByUserId(userId))
            throw CustomException(ErrorCode.INVALID_USER_ID)

        if (restInfo.ownerId != userId) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        return reservationRepository.findByRestKeyOrderByDate(restInfo.restKey!!).stream().map {
            // 고객 전화 번호
            val phone = userInfoRepository.findPhoneByUserId(it.userId)!!

            ReservationInfoDto.fromEntity(it, phone)
        }.toList()
    }
}