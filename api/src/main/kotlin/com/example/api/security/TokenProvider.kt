package com.example.api.security

import com.example.api.exception.CustomException
import com.example.api.exception.ErrorCode
import com.example.api.service.UserService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import java.util.*

@Component
class TokenProvider(
    private val userDetailsService: UserService
) {
    companion object {
        const val KEY_ROLES = "roles"
        const val TOKEN_EXPIRE_TIME = 1000 * 60 * 60
    }

    @Value("\${spring.jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${spring.jwt.prefix}")
    private lateinit var tokenPrefix: String

    fun generateToken(userName: String, roles: String): String {
        val claims = Jwts.claims().setSubject(userName)
        claims[KEY_ROLES] = roles

        val now = Date()
        val expiredDate = Date(now.time + TOKEN_EXPIRE_TIME)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiredDate)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    // 토큰이 유효한지 확인하고 유저 아이디를 추출합니다.
    fun resolveTokenFromHeader(request: HttpHeaders): String {
        val token: String? = request.getFirst(JwtAuthenticationFilter.TOKEN_HEADER)

        if (token.isNullOrEmpty())
            throw CustomException(ErrorCode.INVALID_TOKEN)

        return if (!ObjectUtils.isEmpty(token) && token.startsWith(tokenPrefix)) {
            getSubject(token.substring(tokenPrefix.length))
        } else
            throw CustomException(ErrorCode.INVALID_TOKEN)
    }

    fun getSubject(token: String): String {
        return parseClaims(token).subject
    }

    fun validateToken(token: String): Boolean {
        if (!StringUtils.hasText(token))
            return false

        return !parseClaims(token).expiration.before(Date())
    }

    fun getAuthentication(jwt: String): Authentication {
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(getSubject(jwt))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }
}