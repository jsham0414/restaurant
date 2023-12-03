package com.example.api.service

import com.example.api.dto.RestInfoDto
import com.example.api.dto.RestModify
import com.example.api.dto.RestRegister
import com.example.api.exception.CustomException
import com.example.api.exception.ErrorCode
import com.example.api.security.TokenProvider
import com.example.database.domain.RestaurantInfo
import com.example.database.repository.ReservationRepository
import com.example.database.repository.RestInfoRepository
import com.example.database.repository.ReviewInfoRepository
import com.example.database.repository.UserInfoRepository
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RestaurantService(
    private val restInfoRepository: RestInfoRepository,
    private val reserveInfoRepository: ReservationRepository,
    private val reviewInfoRepository: ReviewInfoRepository,
    private val userInfoRepository: UserInfoRepository,
    private val tokenProvider: TokenProvider
) {
    // 신규 매장 등록
    @Transactional
    fun registerRestaurant(header: HttpHeaders, request: RestRegister.Request): RestRegister.Response {
        val ownerId = tokenProvider.resolveTokenFromHeader(header)

        if (!userInfoRepository.existsByUserId(ownerId))
            throw CustomException(ErrorCode.INVALID_USER_ID)

        val restInfo = restInfoRepository.save(request.toEntity(ownerId))

        if (restInfo.restKey == null)
            throw CustomException(ErrorCode.UNEXPECTED_ERROR)

        return RestRegister.Response(restInfo.restKey!!)
    }

    // 이름으로 매장 리스트 별점 내림차순 반환
    fun findRestaurantsToName(name: String): List<RestInfoDto> {
        return restInfoRepository.findByNameContainsOrderByAverageStarDesc(name)
            .stream().map { RestInfoDto.fromEntity(it) }
            .toList()
    }

    // 주소로 매장 리스트 별점 내림차순 반환
    fun findRestaurantsToAddress(address: String): List<RestInfoDto> {
        return restInfoRepository.findByAddressContainsOrderByAverageStarDesc(address)
            .stream().map { RestInfoDto.fromEntity(it) }
            .toList()
    }

    // 매장 키로 매장 정보 반환
    fun getRestaurant(restKey: UUID): RestInfoDto {
        return RestInfoDto.fromEntity(
            restInfoRepository.findByRestKey(restKey)
                ?: throw CustomException(ErrorCode.INVALID_REST_KEY)
        )
    }

    // 계정에 묶여있는 매장 정보 가져오기
    fun getRestaurantsByOwner(header: HttpHeaders): List<RestaurantInfo> {
        val userId = tokenProvider.resolveTokenFromHeader(header)

        if (!userInfoRepository.existsByUserId(userId))
            throw CustomException(ErrorCode.INVALID_USER_ID)

        return restInfoRepository.findByOwnerId(userId)
    }

    // 매장 삭제
    @Transactional
    fun removeRestaurant(header: HttpHeaders, restKey: UUID) {
        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(restKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 매장 관리자가 아닐 경우
        if (restInfo.ownerId != userId)
            throw CustomException(ErrorCode.DELETE_KEY_NOT_SAME)

        // 모든 예약과 리뷰를 삭제
        val reserveInfoList = reserveInfoRepository.findByRestKeyOrderByDate(restInfo.restKey!!)
        for (reserveInfo in reserveInfoList) {
            val reserveKey = reserveInfo.reserveKey!!

            reserveInfoRepository.delete(reserveInfo)
            val reviewInfo = reviewInfoRepository.findByReserveKey(reserveKey)
                ?: continue

            reviewInfoRepository.delete(reviewInfo)
        }

        restInfoRepository.delete(restInfo)
    }

    // 매장 정보 수정
    @Transactional
    fun modifyRestaurantInfo(header: HttpHeaders, request: RestModify.Request, restKey: UUID) {
        // 매장 정보를 찾을 수 없을 경우
        val restInfo = restInfoRepository.findByRestKey(restKey)
            ?: throw CustomException(ErrorCode.INVALID_RESERVATION)

        val userId = tokenProvider.resolveTokenFromHeader(header)

        // 매장 관리자가 아닐 경우
        if (restInfo.ownerId != userId)
            throw CustomException(ErrorCode.MODIFY_KEY_NOT_SAME)

        if (!request.name.isNullOrEmpty())
            restInfo.name = request.name
        if (!request.address.isNullOrEmpty())
            restInfo.address = request.address
        if (!request.description.isNullOrEmpty())
            restInfo.description = request.description
        if (!request.phone.isNullOrEmpty())
            restInfo.phone = request.phone

        restInfoRepository.save(restInfo)
    }

}