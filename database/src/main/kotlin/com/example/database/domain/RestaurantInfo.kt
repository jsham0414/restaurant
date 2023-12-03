package com.example.database.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "REST_INFO")
class RestaurantInfo(
    // 매장 이름
    @Column(name = "name")
    var name: String,

    // 매장 주소
    @Column(name = "address")
    var address: String,

    // 매장 설명
    @Column(name = "description")
    var description: String,

    // 관리자 아이디
    @Column(name = "owner_id")
    var ownerId: String,

    // 관리자 아이디
    @Column(name = "phone")
    var phone: String
) {
    @Id
    @Column(name = "rest_key")
    @GeneratedValue(strategy = GenerationType.UUID)
    val restKey: UUID? = null

    // 평균 별점
    @Column(name = "average_star")
    var averageStar: Double = 0.0
}