package com.familyapp.data.api

import com.familyapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface FamilyApiService {

    // ── Auth ────────────────────────────────────────
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ── Family ──────────────────────────────────────
    @GET("family")
    suspend fun getFamily(): Response<FamilyResponse>

    @GET("family/members")
    suspend fun getFamilyMembers(): Response<List<UserResponse>>

    // ── Calendar ────────────────────────────────────
    @GET("calendar")
    suspend fun getCalendarEvents(): Response<List<CalendarEventResponse>>

    @POST("calendar")
    suspend fun createCalendarEvent(@Body request: CalendarEventRequest): Response<CalendarEventResponse>

    @DELETE("calendar/{id}")
    suspend fun deleteCalendarEvent(@Path("id") id: Long): Response<Unit>

    // ── Lists ───────────────────────────────────────
    @GET("lists")
    suspend fun getLists(): Response<List<TodoListResponse>>

    @POST("lists")
    suspend fun createList(@Body request: CreateListRequest): Response<TodoListResponse>

    @GET("lists/{listId}/items")
    suspend fun getListItems(@Path("listId") listId: Long): Response<List<ListItemResponse>>

    @POST("lists/{listId}/items")
    suspend fun addListItem(@Path("listId") listId: Long, @Body request: ListItemRequest): Response<ListItemResponse>

    @PUT("lists/items/{itemId}/toggle")
    suspend fun toggleListItem(@Path("itemId") itemId: Long): Response<ListItemResponse>

    @DELETE("lists/items/{itemId}")
    suspend fun deleteListItem(@Path("itemId") itemId: Long): Response<Unit>

    // ── Expenses ────────────────────────────────────
    @GET("expenses")
    suspend fun getExpenses(): Response<List<ExpenseResponse>>

    @POST("expenses")
    suspend fun addExpense(@Body request: ExpenseRequest): Response<ExpenseResponse>

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(@Path("id") id: Long): Response<Unit>

    @GET("expenses/savings")
    suspend fun getSavingsGoals(): Response<List<SavingsGoalResponse>>

    @POST("expenses/savings")
    suspend fun createSavingsGoal(@Body request: SavingsGoalRequest): Response<SavingsGoalResponse>

    // ── Meal Plans ──────────────────────────────────
    @GET("meals")
    suspend fun getMealPlans(): Response<List<MealPlanResponse>>

    @POST("meals")
    suspend fun createMealPlan(@Body request: MealPlanRequest): Response<MealPlanResponse>

    @DELETE("meals/{id}")
    suspend fun deleteMealPlan(@Path("id") id: Long): Response<Unit>

    // ── Messages ────────────────────────────────────
    @GET("messages")
    suspend fun getMessages(): Response<List<MessageResponse>>

    @POST("messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<MessageResponse>

    // ── Notice Board ────────────────────────────────
    @GET("noticeboard")
    suspend fun getNotices(): Response<List<NoticeBoardResponse>>

    @POST("noticeboard")
    suspend fun createNotice(@Body request: NoticeBoardRequest): Response<NoticeBoardResponse>

    @PUT("noticeboard/{id}/pin")
    suspend fun togglePin(@Path("id") id: Long): Response<NoticeBoardResponse>

    @DELETE("noticeboard/{id}")
    suspend fun deleteNotice(@Path("id") id: Long): Response<Unit>

    // ── Location ────────────────────────────────────
    @POST("location")
    suspend fun updateLocation(@Body request: LocationUpdateRequest): Response<Unit>

    @GET("location/family")
    suspend fun getFamilyLocations(): Response<List<UserResponse>>
}
