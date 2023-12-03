package com.example.api.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider
) : OncePerRequestFilter() {
    companion object {
        const val TOKEN_HEADER = "Authorization"
    }

    @Value("\${spring.jwt.prefix}")
    private lateinit var tokenPrefix: String

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token: String? = resolveTokenFromRequest(request)

        // 토큰 유효성 검증
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token!!)) {
            // 유효하다면 인증 정보를 컨텍스트에 담는다.
            val auth = tokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = auth
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveTokenFromRequest(request: HttpServletRequest): String? {
        val token = request.getHeader(TOKEN_HEADER)
        return if (!ObjectUtils.isEmpty(token) && token.startsWith(tokenPrefix)) {
            token.substring(tokenPrefix.length)
        } else null
    }


}