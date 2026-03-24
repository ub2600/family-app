package com.familyapp.dto

import com.familyapp.model.MealType
import com.familyapp.model.MessageType
import com.familyapp.model.UserRole
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

// ── Auth DTOs ──────────────────────────────────────────────

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val familyName: String? = null,
    val inviteCode: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val name: String,
    val email: String,
    val role: String,
    val familyId: Long?,
    val familyName: String?
)

// ── User DTOs ──────────────────────────────────────────────

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val avatarUrl: String?,
    val familyId: Long?,
    val familyName: String?,
    val active: Boolean,
    val lastLatitude: Double?,
    val lastLongitude: Double?,
    val lastLocationUpdate: LocalDateTime?,
    val createdAt: LocalDateTime
)

data class LocationUpdateRequest(
    val latitude: Double,
    val longitude: Double
)

// ── Family DTOs ────────────────────────────────────────────

data class FamilyResponse(
    val id: Long,
    val name: String,
    val inviteCode: String,
    val memberCount: Int,
    val createdAt: LocalDateTime
)

data class CreateFamilyRequest(
    val name: String
)

// ── Calendar DTOs ──────────────────────────────────────────

data class CalendarEventRequest(
    val title: String,
    val description: String? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val color: String = "#4A90D9",
    val allDay: Boolean = false
)

data class CalendarEventResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val color: String,
    val allDay: Boolean,
    val createdByName: String?,
    val createdAt: LocalDateTime
)

// ── List DTOs ──────────────────────────────────────────────

data class CreateListRequest(
    val name: String,
    val icon: String = "📋"
)

data class TodoListResponse(
    val id: Long,
    val name: String,
    val icon: String,
    val itemCount: Int,
    val completedCount: Int,
    val createdByName: String?,
    val createdAt: LocalDateTime
)

data class ListItemRequest(
    val text: String,
    val quantity: Int? = null,
    val notes: String? = null
)

data class ListItemResponse(
    val id: Long,
    val text: String,
    val completed: Boolean,
    val quantity: Int?,
    val notes: String?,
    val completedByName: String?,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime
)

// ── Expense DTOs ───────────────────────────────────────────

data class ExpenseRequest(
    val amount: BigDecimal,
    val description: String,
    val category: String = "General",
    val date: LocalDate = LocalDate.now()
)

data class ExpenseResponse(
    val id: Long,
    val amount: BigDecimal,
    val description: String,
    val category: String,
    val date: LocalDate,
    val paidByName: String?,
    val createdAt: LocalDateTime
)

data class SavingsGoalRequest(
    val name: String,
    val targetAmount: BigDecimal,
    val icon: String = "🎯"
)

data class SavingsGoalResponse(
    val id: Long,
    val name: String,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val icon: String,
    val progressPercent: Int,
    val createdAt: LocalDateTime
)

// ── Meal Plan DTOs ─────────────────────────────────────────

data class MealPlanRequest(
    val date: LocalDate,
    val mealType: MealType,
    val recipeName: String,
    val recipeUrl: String? = null,
    val notes: String? = null,
    val servings: Int = 4
)

data class MealPlanResponse(
    val id: Long,
    val date: LocalDate,
    val mealType: String,
    val recipeName: String,
    val recipeUrl: String?,
    val notes: String?,
    val servings: Int,
    val createdByName: String?,
    val createdAt: LocalDateTime
)

// ── Message DTOs ───────────────────────────────────────────

data class SendMessageRequest(
    val content: String?,
    val mediaUrl: String? = null,
    val messageType: MessageType = MessageType.TEXT
)

data class MessageResponse(
    val id: Long,
    val content: String?,
    val mediaUrl: String?,
    val messageType: String,
    val senderName: String,
    val senderId: Long,
    val timestamp: LocalDateTime
)

// ── Notice Board DTOs ──────────────────────────────────────

data class NoticeBoardRequest(
    val title: String,
    val content: String,
    val pinned: Boolean = false,
    val color: String = "#FFD700"
)

data class NoticeBoardResponse(
    val id: Long,
    val title: String,
    val content: String,
    val pinned: Boolean,
    val color: String,
    val createdByName: String?,
    val createdAt: LocalDateTime
)

// ── Admin Dashboard DTOs ───────────────────────────────────

data class DashboardStats(
    val totalFamilies: Long,
    val totalUsers: Long,
    val activeUsers: Long,
    val totalEvents: Long,
    val totalMessages: Long,
    val totalExpenses: BigDecimal
)
