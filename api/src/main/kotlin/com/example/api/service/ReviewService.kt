package com.example.api.service

import com.example.api.dto.ModifyReview
import com.example.api.dto.RegisterReview
import com.example.api.dto.ReviewInfoDto
import com.example.api.exception.CustomException
import com.example.api.exception.ErrorCode
import com.example.api.security.TokenProvider
import com.example.database.domain.ReviewInfo
import com.example.database.model.constant.ReservationStatus
import com.example.database.repository.ReservationRepository
import com.example.database.repository.RestInfoRepository
import com.example.database.repository.ReviewInfoRepository
import com.example.database.repository.UserInfoRepository
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ReviewService(
    private val reviewInfoRepository: ReviewInfoRepository,
    private val reserveInfoRepository: ReservationRepository,
    private val restInfoRepository: RestInfoRepository,
    private val userInfoRepository: UserInfoRepository,
    private val tokenProvider: TokenProvider
) {
    // 별점 업데이트
    @Transactional
    @Scheduled(cron = "\${scheduler.review.update}")
    fun updateStar() {
        val restInfoList = restInfoRepository.findAll()

        for (restInfo in restInfoList) {
            var sum = 0.0
            var count = 0

            val reserveInfoList = reserveInfoRepository.findByRestKeyOrderByDate(restInfo.restKey!!)

            for (reserveInfo in reserveInfoList) {
                // 리뷰가 작성되어 있지 않으면 넘긴다.
                val reviewInfo = reviewInfoRepository.findByReserveKey(reserveInfo.reserveKey!!)
                    ?: continue

                sum += reviewInfo.star
                count++
            }

            if (count == 0)
                continue

            // 5점 만점
            restInfo.averageStar = (sum / count) / 2

            restInfoRepository.save(restInfo)
            Thread.sleep(1000)
        }
    }

    // 리뷰 생성
    @Transactional
    fun register(header: HttpHeaders, request: RegisterReview.Request): UUID {
        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 이미 작성한 리뷰일 경우
        if (reviewInfoRepository.existsByReserveKey(request.reserveKey)) {
            throw CustomException(ErrorCode.WRITTEN_REVIEW)
        }

        // 예약 자체가 없을 경우
        val reserveInfo = reserveInfoRepository.findByReserveKey(request.reserveKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        // 예약이 끝나지 않았을 경우
        if (reserveInfo.status != ReservationStatus.CONFIRMED) {
            throw CustomException(ErrorCode.RESERVATION_NOT_FINISHED)
        }

        // 예약한 본인이 아닐 경우
        if (userId != reserveInfo.userId) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        val reviewInfo = ReviewInfo(
            reserveKey = request.reserveKey,
            star = request.star.coerceIn(0..10),
            comment = request.comment
        )

        return reviewInfoRepository.save(reviewInfo).reviewKey!!
    }

    // 리뷰 수정
    @Transactional
    fun modify(header: HttpHeaders, request: ModifyReview.Request) {
        // 수정 할 리뷰가 없을 경우
        val reviewInfo = reviewInfoRepository.findByReviewKey(request.reviewKey)
            ?: throw CustomException(ErrorCode.INVALID_REVIEW)

        // 예약 자체가 없을 경우
        val reserveInfo = reserveInfoRepository.findByReserveKey(reviewInfo.reserveKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 예약한 본인이 아닐 경우
        if (userId != reserveInfo.userId) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        // 값이 들어온 정보만 수정
        if (!request.comment.isNullOrEmpty())
            reviewInfo.comment = request.comment
        if (request.star != null)
            reviewInfo.star = request.star.coerceIn(0..10)

        reviewInfoRepository.save(reviewInfo)
    }

    // 리뷰 삭제
    @Transactional
    fun deleteByCustomer(header: HttpHeaders, reviewKey: UUID) {
        // 리뷰를 찾을 수 없을 경우
        val reviewInfo = reviewInfoRepository.findByReviewKey(reviewKey)
            ?: throw CustomException(ErrorCode.INVALID_REVIEW)

        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 예약 정보를 찾을 수 없을 경우
        val reserveInfo = reserveInfoRepository.findByReserveKey(reviewInfo.reserveKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        // 자신이 작성한 리뷰가 아닐 경우
        if (userId != reserveInfo.userId) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        reviewInfoRepository.delete(reviewInfo)
    }

    // 리뷰 삭제 (매장 관리자)
    @Transactional
    fun deleteByOwner(header: HttpHeaders, reviewKey: UUID) {
        // 리뷰가 없을 경우
        val reviewInfo = reviewInfoRepository.findByReviewKey(reviewKey)
            ?: throw CustomException(ErrorCode.INVALID_REVIEW)

        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 유효한 예약 정보가 아닐 경우
        val reserveInfo = reserveInfoRepository.findByReserveKey(reviewInfo.reserveKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(reserveInfo.restKey)
            ?: throw CustomException(ErrorCode.INVALID_REST_KEY)

        // 매장 관리자가 아닐 경우
        if (userId != restInfo.ownerId) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        reviewInfoRepository.delete(reviewInfo)
    }

    // 고객용 리뷰 가져오기
    fun getReviewByCustomer(header: HttpHeaders): List<Any> {
        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 유효한 유저 아이디가 아닐 경우
        if (userInfoRepository.existsByUserId(userId))
            throw CustomException(ErrorCode.INVALID_USER_ID)

        val reserveInfoList = reserveInfoRepository.findByUserId(userId)

        val reviewInfoList = mutableListOf<ReviewInfoDto>()

        for (reserveInfo in reserveInfoList) {
            // 리뷰가 아직 작성되지 않았을 경우
            val reviewInfo = reviewInfoRepository.findByReserveKey(reserveInfo.reserveKey!!)
                ?: continue

            reviewInfoList.add(ReviewInfoDto.fromEntity(reviewInfo))
        }

        return reviewInfoList
    }

    // 리뷰 가져오기 (매장 관리자)
    fun getReviewByOwner(header: HttpHeaders, restKey: UUID): List<Any> {
        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(restKey)
            ?: throw CustomException(ErrorCode.INVALID_REST_KEY)

        // 매장 관리자가 아닐 경우
        if (userId != restInfo.ownerId) {
            throw CustomException(ErrorCode.NOT_AUTHORIZED)
        }

        val reserveInfoList = reserveInfoRepository.findByRestKeyOrderByDate(restKey)

        val reviewInfoList = mutableListOf<ReviewInfoDto>()

        for (reserveInfo in reserveInfoList) {
            // 리뷰가 아직 작성되지 않았을 경우
            val reviewInfo = reviewInfoRepository.findByReserveKey(reserveInfo.reserveKey!!)
                ?: continue

            reviewInfoList.add(ReviewInfoDto.fromEntity(reviewInfo))
        }

        return reviewInfoList
    }
}