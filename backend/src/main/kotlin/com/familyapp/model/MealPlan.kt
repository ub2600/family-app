package com.familyapp.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
}

@Entity
@Table(name = "meal_plans")
data class MealPlan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var date: LocalDate = LocalDate.now(),

    @Enumerated(EnumType.STRING)
    var mealType: MealType = MealType.DINNER,

    @Column(nullable = false)
    var recipeName: String = "",

    var recipeUrl: String? = null,

    var notes: String? = null,

    var imageUrl: String? = null,

    var servings: Int = 4,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    var createdBy: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    var family: Family? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
