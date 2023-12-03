package com.example.api.controller

import com.example.api.dto.RestModify
import com.example.api.dto.RestRegister
import com.example.api.service.RestaurantService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/restaurant/api/v1/restaurant")
@Tag(name = "매장 컨트롤러", description = "매장에 관한 다양한 엔드포인트를 제공합니다.")
class RestaurantController(
    private val restaurantService: RestaurantService
) {
    // 매장 등록 (파트너만 가능)
    @PostMapping
    @PreAuthorize("hasRole('PARTNER')")
    @Operation(summary = "매장 등록", description = "파트너 인증 후 매장을 등록합니다.")
    fun registerRestaurant(@RequestHeader header: HttpHeaders, @RequestBody request: RestRegister.Request)
            : ResponseEntity<Any> {
        return ResponseEntity.ok(restaurantService.registerRestaurant(header, request))
    }

    // 주소로 매장 찾기
    @GetMapping("/address/{address}")
    @Operation(summary = "주소로 매장 찾기", description = "주소로 찾은 매장 정보를 별점 순으로 반환합니다.")
    fun findRestaurantsToAddress(@PathVariable address: String)
            : ResponseEntity<List<Any>> {
        return ResponseEntity.ok(restaurantService.findRestaurantsToAddress(address))
    }

    // 이름으로 매장 찾기
    @GetMapping("/name/{name}")
    @Operation(summary = "이름으로 매장 찾기", description = "이름으로 찾은 매장 정보를 별점 순으로 반환합니다.")
    fun findRestaurantsToName(@PathVariable name: String)
            : ResponseEntity<List<Any>> {
        return ResponseEntity.ok(restaurantService.findRestaurantsToName(name))
    }

    // 레스토랑 키를 이용해 정보 반환
    @GetMapping("/{restKey}")
    @Operation(summary = "레스토랑 키로 매장 찾기", description = "레스토랑 키로 매장을 찾습니다.")
    fun findRestaurantByKey(@PathVariable restKey: UUID)
            : ResponseEntity<Any> {
        return ResponseEntity.ok(restaurantService.getRestaurant(restKey))
    }

    // 등록한 매장들을 반환
    @GetMapping("/restaurants")
    @PreAuthorize("hasRole('PARTNER')")
    @Operation(summary = "매장 리스트 반환", description = "본인이 관리자인 매장들을 반환합니다.")
    fun getRestaurantList(@RequestHeader header: HttpHeaders)
            : ResponseEntity<Any> {
        return ResponseEntity.ok(restaurantService.getRestaurantsByOwner(header))
    }

    // 매장 삭제 (해당 매장의 관리자만 가능)
    @DeleteMapping("/{restKey}")
    @PreAuthorize("hasRole('PARTNER')")
    @Operation(summary = "매장 삭제", description = "관리자 인증 후 매장에 대한 모든 정보를 삭제합니다.")
    fun removeRestaurant(@RequestHeader header: HttpHeaders, @PathVariable restKey: UUID)
            : ResponseEntity<Any> {
        return ResponseEntity.ok(restaurantService.removeRestaurant(header, restKey))
    }

    // 매장 정보 수정 (해당 매장의 관리자만 가능하며 입력된 정보만 수정)
    @PutMapping("/{restKey}")
    @PreAuthorize("hasRole('PARTNER')")
    @Operation(summary = "매장 정보 수정", description = "관리자 인증 후 매장 정보를 수정합니다.")
    fun modifyRestaurantInfo(
        @RequestHeader header: HttpHeaders,
        @RequestBody request: RestModify.Request,
        @PathVariable restKey: UUID
    )
            : ResponseEntity<Any> {
        return ResponseEntity.ok(restaurantService.modifyRestaurantInfo(header, request, restKey))
    }
}