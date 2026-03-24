package com.familyapp.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "families")
data class Family(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(unique = true, nullable = false)
    var inviteCode: String = "",

    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "family", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val members: MutableList<User> = mutableListOf()
)
