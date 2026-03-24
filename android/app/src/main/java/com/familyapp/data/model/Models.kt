package com.familyapp.data.model

// ── Auth Models ─────────────────────────────────────
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

// ── User Models ─────────────────────────────────────
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
    val lastLocationUpdate: String?,
    val createdAt: String
)

data class LocationUpdateRequest(
    val latitude: Double,
    val longitude: Double
)

// ── Family Models ───────────────────────────────────
data class FamilyResponse(
    val id: Long,
    val name: String,
    val inviteCode: String,
    val memberCount: Int,
    val createdAt: String
)

// ── Calendar Models ─────────────────────────────────
data class CalendarEventRequest(
    val title: String,
    val description: String? = null,
    val startTime: String,
    val endTime: String? = null,
    val color: String = "#4A90D9",
    val allDay: Boolean = false
)

data class CalendarEventResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String?,
    val color: String,
    val allDay: Boolean,
    val createdByName: String?,
    val createdAt: String
)

// ── List Models ─────────────────────────────────────
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
    val createdAt: String
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
    val completedAt: String?,
    val createdAt: String
)

// ── Expense Models ──────────────────────────────────
data class ExpenseRequest(
    val amount: Double,
    val description: String,
    val category: String = "General",
    val date: String? = null
)

data class ExpenseResponse(
    val id: Long,
    val amount: Double,
    val description: String,
    val category: String,
    val date: String,
    val paidByName: String?,
    val createdAt: String
)

data class SavingsGoalRequest(
    val name: String,
    val targetAmount: Double,
    val icon: String = "🎯"
)

data class SavingsGoalResponse(
    val id: Long,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val icon: String,
    val progressPercent: Int,
    val createdAt: String
)

// ── Meal Plan Models ────────────────────────────────
data class MealPlanRequest(
    val date: String,
    val mealType: String,
    val recipeName: String,
    val recipeUrl: String? = null,
    val notes: String? = null,
    val servings: Int = 4
)

data class MealPlanResponse(
    val id: Long,
    val date: String,
    val mealType: String,
    val recipeName: String,
    val recipeUrl: String?,
    val notes: String?,
    val servings: Int,
    val createdByName: String?,
    val createdAt: String
)

// ── Message Models ──────────────────────────────────
data class SendMessageRequest(
    val content: String?,
    val mediaUrl: String? = null,
    val messageType: String = "TEXT"
)

data class MessageResponse(
    val id: Long,
    val content: String?,
    val mediaUrl: String?,
    val messageType: String,
    val senderName: String,
    val senderId: Long,
    val timestamp: String
)

// ── Notice Board Models ─────────────────────────────
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
    val createdAt: String
)
