package com.familyapp.model

import jakarta.persistence.*
import java.time.LocalDateTime

enum class UserRole {
    ADMIN, PARENT, CHILD
}

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(unique = true, nullable = false)
    var email: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.PARENT,

    var avatarUrl: String? = null,

    var lastLatitude: Double? = null,
    var lastLongitude: Double? = null,
    var lastLocationUpdate: LocalDateTime? = null,

    var active: Boolean = true,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    var family: Family? = null
)
