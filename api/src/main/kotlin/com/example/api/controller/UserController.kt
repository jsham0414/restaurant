package com.example.api.controller

import com.example.api.dto.Auth
import com.example.api.security.TokenProvider
import com.example.api.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/restaurant/api/v1")
@Tag(name = "유저 컨트롤러", description = "유저 계정 정보에 관한 엔드포인트를 제공합니다.")
class UserController(
    private val userService: UserService,
    private val tokenProvider: TokenProvider
) {
    // 회원 가입
    @PostMapping("/auth/sign-up")
    @Operation(summary = "회원 가입", description = "아이디 중복 확인 후 생성된 계정 정보를 반환합니다.")
    fun signUp(@RequestBody request: Auth.Request.SignUp)
            : ResponseEntity<Any> {
        return ResponseEntity.ok(userService.register(request))
    }

    // 로그인
    @PostMapping("/auth/sign-in")
    @Operation(summary = "로그인", description = "아이디와 비밀번호를 확인하고 발급된 Jwt를 반환합니다.")
    fun signIn(@RequestBody request: Auth.Request.SignIn)
            : ResponseEntity<Any> {
        val userInfoDto = userService.authenticate(request)
        val token = tokenProvider.generateToken(
            userInfoDto.userId,
            userInfoDto.roles
        )
        return ResponseEntity.ok(token)
    }
}