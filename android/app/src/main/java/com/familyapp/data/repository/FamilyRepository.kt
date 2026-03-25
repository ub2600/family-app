package com.familyapp.data.repository

import android.content.Context
import com.familyapp.data.api.FamilyApiService
import com.familyapp.data.api.PrefsKeys
import com.familyapp.data.api.dataStore
import com.familyapp.data.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.core.edit

@Singleton
class FamilyRepository @Inject constructor(
    private val api: FamilyApiService,
    @ApplicationContext private val context: Context
) {
    // ── Token Management ────────────────────────────
    suspend fun saveToken(auth: AuthResponse) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKeys.TOKEN] = auth.token
            prefs[PrefsKeys.USER_ID] = auth.userId.toString()
            prefs[PrefsKeys.USER_NAME] = auth.name
            auth.familyId?.let { prefs[PrefsKeys.FAMILY_ID] = it.toString() }
        }
    }

    suspend fun getToken(): String = context.dataStore.data.map { it[PrefsKeys.TOKEN] ?: "" }.first()
    suspend fun getUserName(): String = context.dataStore.data.map { it[PrefsKeys.USER_NAME] ?: "" }.first()
    suspend fun getUserId(): Long = context.dataStore.data.map { (it[PrefsKeys.USER_ID] ?: "0").toLong() }.first()
    suspend fun isLoggedIn(): Boolean = getToken().isNotEmpty()

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }

    // ── Auth ────────────────────────────────────────
    suspend fun login(email: String, password: String): Result<AuthResponse> = apiCall {
        api.login(LoginRequest(email, password))
    }

    suspend fun register(name: String, email: String, password: String, familyName: String?, inviteCode: String?): Result<AuthResponse> = apiCall {
        api.register(RegisterRequest(name, email, password, familyName, inviteCode))
    }

    // ── Calendar ────────────────────────────────────
    suspend fun getCalendarEvents(): Result<List<CalendarEventResponse>> = apiCall { api.getCalendarEvents() }
    suspend fun createCalendarEvent(request: CalendarEventRequest): Result<CalendarEventResponse> = apiCall { api.createCalendarEvent(request) }
    suspend fun deleteCalendarEvent(id: Long): Result<Unit> = apiCall { api.deleteCalendarEvent(id) }

    // ── Lists ───────────────────────────────────────
    suspend fun getLists(): Result<List<TodoListResponse>> = apiCall { api.getLists() }
    suspend fun createList(request: CreateListRequest): Result<TodoListResponse> = apiCall { api.createList(request) }
    suspend fun getListItems(listId: Long): Result<List<ListItemResponse>> = apiCall { api.getListItems(listId) }
    suspend fun addListItem(listId: Long, request: ListItemRequest): Result<ListItemResponse> = apiCall { api.addListItem(listId, request) }
    suspend fun toggleListItem(itemId: Long): Result<ListItemResponse> = apiCall { api.toggleListItem(itemId) }
    suspend fun deleteListItem(itemId: Long): Result<Unit> = apiCall { api.deleteListItem(itemId) }

    // ── Expenses ────────────────────────────────────
    suspend fun getExpenses(): Result<List<ExpenseResponse>> = apiCall { api.getExpenses() }
    suspend fun addExpense(request: ExpenseRequest): Result<ExpenseResponse> = apiCall { api.addExpense(request) }
    suspend fun deleteExpense(id: Long): Result<Unit> = apiCall { api.deleteExpense(id) }
    suspend fun getSavingsGoals(): Result<List<SavingsGoalResponse>> = apiCall { api.getSavingsGoals() }
    suspend fun createSavingsGoal(request: SavingsGoalRequest): Result<SavingsGoalResponse> = apiCall { api.createSavingsGoal(request) }

    // ── Meals ───────────────────────────────────────
    suspend fun getMealPlans(): Result<List<MealPlanResponse>> = apiCall { api.getMealPlans() }
    suspend fun createMealPlan(request: MealPlanRequest): Result<MealPlanResponse> = apiCall { api.createMealPlan(request) }
    suspend fun deleteMealPlan(id: Long): Result<Unit> = apiCall { api.deleteMealPlan(id) }

    // ── Messages ────────────────────────────────────
    suspend fun getMessages(): Result<List<MessageResponse>> = apiCall { api.getMessages() }
    suspend fun sendMessage(request: SendMessageRequest): Result<MessageResponse> = apiCall { api.sendMessage(request) }

    // ── Notice Board ────────────────────────────────
    suspend fun getNotices(): Result<List<NoticeBoardResponse>> = apiCall { api.getNotices() }
    suspend fun createNotice(request: NoticeBoardRequest): Result<NoticeBoardResponse> = apiCall { api.createNotice(request) }
    suspend fun togglePin(id: Long): Result<NoticeBoardResponse> = apiCall { api.togglePin(id) }
    suspend fun deleteNotice(id: Long): Result<Unit> = apiCall { api.deleteNotice(id) }

    // ── Location ────────────────────────────────────
    suspend fun updateLocation(lat: Double, lng: Double): Result<Unit> = apiCall { api.updateLocation(LocationUpdateRequest(lat, lng)) }
    suspend fun getFamilyLocations(): Result<List<UserResponse>> = apiCall { api.getFamilyLocations() }

    // ── Helper ──────────────────────────────────────
    private suspend fun <T> apiCall(call: suspend () -> retrofit2.Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
