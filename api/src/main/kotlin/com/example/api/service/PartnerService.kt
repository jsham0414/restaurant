package com.example.api.service

import com.example.api.exception.CustomException
import com.example.api.exception.ErrorCode
import com.example.api.security.TokenProvider
import com.example.database.domain.UserInfo
import com.example.database.model.constant.Authority
import com.example.database.repository.UserInfoRepository
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PartnerService(
    private val userInfoRepository: UserInfoRepository,
    private val tokenProvider: TokenProvider
) {
    // 파트너 등록
    @Transactional
    @Throws(CustomException::class)
    fun assignPartner(header: HttpHeaders) {
        val userId = tokenProvider.resolveTokenFromHeader(header)

        val userInfo: UserInfo = userInfoRepository.findByUserId(userId)
            ?: throw CustomException(ErrorCode.INVALID_USER_ID)

        // 이미 파트너일 경우
        if (userInfo.roles.compareTo(Authority.ROLE_PARTNER.toString()) == 0)
            throw CustomException(ErrorCode.ALREADY_PARTNER)

        userInfo.roles = Authority.ROLE_PARTNER.toString()

        userInfoRepository.save(userInfo)
    }


}