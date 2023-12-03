package com.example.api.service

import com.example.api.dto.Auth
import com.example.api.dto.UserInfoDto
import com.example.api.exception.CustomException
import com.example.api.exception.ErrorCode
import com.example.database.domain.UserInfo
import com.example.database.model.constant.Authority
import com.example.database.repository.UserInfoRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userInfoRepository: UserInfoRepository
) : UserDetailsService {
    @Transactional
    @Throws(CustomException::class)
    // 회원 가입
    fun register(request: Auth.Request.SignUp): Auth.Response.SignUp {
        // 이미 존재하는 아이디일 경우
        if (userInfoRepository.existsByUserId(request.userId)) {
            throw CustomException(ErrorCode.USER_ID_EXISTS)
        }

        val userInfo = userInfoRepository.save(
            request.toEntity(
                Authority.ROLE_MEMBER.toString()
            )
        )

        return Auth.Response.SignUp(
            userId = userInfo.userId,
            userPw = userInfo.userPw,
            roles = userInfo.roles,
            phone = userInfo.phone
        )
    }

    // 로그인 인증
    @Throws(CustomException::class)
    fun authenticate(request: Auth.Request.SignIn): UserInfoDto {
        // 유저 아이디로 계정을 찾을 수 없을 경우
        val userInfo: UserInfo =
            userInfoRepository.findByUserId(request.userId) ?: throw CustomException(ErrorCode.USER_NOT_FOUNDED)

        // 패스워드가 틀렸을 경우
        if (request.userPw.compareTo(userInfo.userPw) != 0) {
            throw CustomException(ErrorCode.PASSWORD_NOT_MATCHED)
        }

        return UserInfoDto(
            userId = userInfo.userId,
            userPw = userInfo.userPw,
            roles = userInfo.roles,
            phone = userInfo.phone
        )
    }

    @Throws(CustomException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return userInfoRepository.findByUserId(username)
            ?: throw UsernameNotFoundException("couldn't find user -> $username")
    }
}