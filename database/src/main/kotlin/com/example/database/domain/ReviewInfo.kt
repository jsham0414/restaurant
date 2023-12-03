package com.example.database.domain

import jakarta.persistence.*
import org.hibernate.validator.constraints.Range
import java.util.*

@Entity
@Table(name = "REVIEW")
class ReviewInfo(
    @Column(name = "reserve_key")
    val reserveKey: UUID,

    @Range(min = 0, max = 10)
    @Column(name = "star")
    var star: Int,

    @Column(name = "comment")
    var comment: String
) {
    @Id
    @Column(name = "review_key")
    @GeneratedValue(strategy = GenerationType.UUID)
    val reviewKey: UUID? = null
}