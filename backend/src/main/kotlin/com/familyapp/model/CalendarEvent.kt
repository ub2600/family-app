package com.familyapp.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "calendar_events")
data class CalendarEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String = "",

    var description: String? = null,

    @Column(nullable = false)
    var startTime: LocalDateTime = LocalDateTime.now(),

    var endTime: LocalDateTime? = null,

    var color: String = "#4A90D9",

    var allDay: Boolean = false,

    var recurring: Boolean = false,

    var recurrenceRule: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    var createdBy: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    var family: Family? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
