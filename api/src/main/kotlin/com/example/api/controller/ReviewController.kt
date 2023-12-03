package com.example.api.com.example.api.controller

import com.example.api.dto.ModifyReview
import com.example.api.dto.RegisterReview
import com.example.api.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/restaurant/api/v1/review")
@Tag(name = "리뷰 컨트롤러", description = "리뷰에 관한 다양한 엔드포인트를 제공합니다.")
class ReviewController(
    private val reviewService: ReviewService
) {
    @GetMapping("/member")
    @Operation(summary = "고객 리뷰 리스트 반환", description = "고객 인증 후 작성한 모든 리뷰를 반환합니다.")
    fun getReviewByCustomer(@RequestHeader header: HttpHeaders): ResponseEntity<Any> {
        return ResponseEntity.ok(reviewService.getReviewByCustomer(header))
    }

    @GetMapping("/partner/{restKey}")
    @Operation(summary = "관리자 리뷰 리스트 반환", description = "관리자 인증 후 매장에 작성된 모든 리뷰를 반환합니다.")
    fun getReviewByOwner(
        @RequestHeader header: HttpHeaders,
        @RequestParam restKey: UUID
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(reviewService.getReviewByOwner(header, restKey))
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'PARTNER')")
    @Operation(summary = "리뷰 등록", description = "고객 인증 후 리뷰를 등록합니다.")
    fun register(
        @RequestHeader header: HttpHeaders,
        @RequestBody registerReview: RegisterReview.Request
    )
            : ResponseEntity<Any> {
        return ResponseEntity.ok(reviewService.register(header, registerReview))
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'PARTNER')")
    @Operation(summary = "리뷰 수정", description = "고객 인증 후 리뷰를 수정합니다.")
    fun modify(
        @RequestHeader header: HttpHeaders,
        @RequestBody registerReview: ModifyReview.Request
    ) {
        reviewService.modify(header, registerReview)
    }

    @DeleteMapping("/member/{reviewKey}")
    @PreAuthorize("hasAnyRole('MEMBER', 'PARTNER')")
    @Operation(summary = "리뷰 삭제 (고객)", description = "고객 인증 후 리뷰를 삭제합니다.")
    fun deleteByCustomer(
        @RequestHeader header: HttpHeaders,
        @RequestParam reviewKey: UUID
    ) {
        reviewService.deleteByCustomer(header, reviewKey)
    }

    @DeleteMapping("/partner/{reviewKey}")
    @PreAuthorize("hasAnyRole('PARTNER')")
    @Operation(summary = "리뷰 삭제 (관리자)", description = "관리자 인증 후 리뷰를 삭제합니다.")
    fun deleteByOwner(
        @RequestHeader header: HttpHeaders,
        @RequestParam reviewKey: UUID
    ) {
        reviewService.deleteByOwner(header, reviewKey)
    }
}