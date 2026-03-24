package com.familyapp.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "expenses")
data class Expense(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var description: String = "",

    var category: String = "General",

    var date: LocalDate = LocalDate.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    var paidBy: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    var family: Family? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "savings_goals")
data class SavingsGoal(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false, precision = 10, scale = 2)
    var targetAmount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2)
    var currentAmount: BigDecimal = BigDecimal.ZERO,

    var icon: String = "🎯",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    var family: Family? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
