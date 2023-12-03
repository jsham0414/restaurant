package com.example.api.controller

import com.example.api.service.PartnerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "파트너 컨트롤러", description = "파트너 등록을 위한 엔드포인트를 제공합니다.")
@RequestMapping("/restaurant/api/v1/partner")
class PartnerController(
    private val partnerService: PartnerService
) {
    // 파트너 등록
    @PostMapping("/assign")
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "파트너 등록", description = "유저의 권한을 파트너로 변경합니다.")
    fun assignPartner(@RequestHeader header: HttpHeaders) {
        partnerService.assignPartner(header)
    }
}