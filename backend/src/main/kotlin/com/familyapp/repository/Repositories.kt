package com.familyapp.repository

import com.familyapp.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByFamilyId(familyId: Long): List<User>
    fun countByActive(active: Boolean): Long
    fun findByRole(role: UserRole): List<User>
}

@Repository
interface FamilyRepository : JpaRepository<Family, Long> {
    fun findByInviteCode(inviteCode: String): Family?
}

@Repository
interface CalendarEventRepository : JpaRepository<CalendarEvent, Long> {
    fun findByFamilyIdOrderByStartTimeAsc(familyId: Long): List<CalendarEvent>
    fun findByFamilyIdAndStartTimeBetween(familyId: Long, start: LocalDateTime, end: LocalDateTime): List<CalendarEvent>
}

@Repository
interface TodoListRepository : JpaRepository<TodoList, Long> {
    fun findByFamilyId(familyId: Long): List<TodoList>
}

@Repository
interface ListItemRepository : JpaRepository<ListItem, Long> {
    fun findByTodoListId(todoListId: Long): List<ListItem>
    fun countByTodoListIdAndCompleted(todoListId: Long, completed: Boolean): Long
}

@Repository
interface ExpenseRepository : JpaRepository<Expense, Long> {
    fun findByFamilyIdOrderByDateDesc(familyId: Long): List<Expense>
    fun findByFamilyIdAndDateBetween(familyId: Long, start: LocalDate, end: LocalDate): List<Expense>

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.family.id = :familyId")
    fun sumByFamilyId(familyId: Long): java.math.BigDecimal?
}

@Repository
interface SavingsGoalRepository : JpaRepository<SavingsGoal, Long> {
    fun findByFamilyId(familyId: Long): List<SavingsGoal>
}

@Repository
interface MealPlanRepository : JpaRepository<MealPlan, Long> {
    fun findByFamilyIdAndDateBetweenOrderByDateAscMealTypeAsc(familyId: Long, start: LocalDate, end: LocalDate): List<MealPlan>
    fun findByFamilyIdOrderByDateDesc(familyId: Long): List<MealPlan>
}

@Repository
interface MessageRepository : JpaRepository<Message, Long> {
    fun findByFamilyIdOrderByTimestampDesc(familyId: Long): List<Message>
    fun findTop50ByFamilyIdOrderByTimestampDesc(familyId: Long): List<Message>
}

@Repository
interface NoticeBoardRepository : JpaRepository<NoticeBoard, Long> {
    fun findByFamilyIdOrderByPinnedDescCreatedAtDesc(familyId: Long): List<NoticeBoard>
}
