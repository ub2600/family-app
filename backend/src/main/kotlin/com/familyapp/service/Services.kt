package com.familyapp.service

import com.familyapp.config.JwtUtil
import com.familyapp.dto.*
import com.familyapp.model.*
import com.familyapp.repository.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val familyRepository: FamilyRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already registered")
        }

        val family = when {
            request.inviteCode != null -> {
                familyRepository.findByInviteCode(request.inviteCode)
                    ?: throw IllegalArgumentException("Invalid invite code")
            }
            request.familyName != null -> {
                familyRepository.save(
                    Family(
                        name = request.familyName,
                        inviteCode = UUID.randomUUID().toString().substring(0, 8).uppercase()
                    )
                )
            }
            else -> throw IllegalArgumentException("Either familyName or inviteCode is required")
        }

        val user = userRepository.save(
            User(
                name = request.name,
                email = request.email,
                password = passwordEncoder.encode(request.password),
                role = if (request.inviteCode != null) UserRole.PARENT else UserRole.PARENT,
                family = family
            )
        )

        val token = jwtUtil.generateToken(user.id, user.email, user.role.name)
        return AuthResponse(token, user.id, user.name, user.email, user.role.name, family.id, family.name)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        if (!user.active) {
            throw IllegalArgumentException("Account is deactivated")
        }

        val token = jwtUtil.generateToken(user.id, user.email, user.role.name)
        return AuthResponse(
            token, user.id, user.name, user.email, user.role.name,
            user.family?.id, user.family?.name
        )
    }
}

@Service
class FamilyService(
    private val familyRepository: FamilyRepository,
    private val userRepository: UserRepository
) {
    fun getFamily(familyId: Long): FamilyResponse {
        val family = familyRepository.findById(familyId).orElseThrow { IllegalArgumentException("Family not found") }
        val members = userRepository.findByFamilyId(familyId)
        return FamilyResponse(family.id, family.name, family.inviteCode, members.size, family.createdAt)
    }

    fun getFamilyMembers(familyId: Long): List<UserResponse> {
        return userRepository.findByFamilyId(familyId).map { it.toResponse() }
    }

    fun getAllFamilies(): List<FamilyResponse> {
        return familyRepository.findAll().map { family ->
            val members = userRepository.findByFamilyId(family.id)
            FamilyResponse(family.id, family.name, family.inviteCode, members.size, family.createdAt)
        }
    }
}

@Service
class CalendarService(
    private val calendarEventRepository: CalendarEventRepository,
    private val familyRepository: FamilyRepository
) {
    fun getEvents(familyId: Long): List<CalendarEventResponse> {
        return calendarEventRepository.findByFamilyIdOrderByStartTimeAsc(familyId).map { it.toResponse() }
    }

    fun getEventsBetween(familyId: Long, start: LocalDateTime, end: LocalDateTime): List<CalendarEventResponse> {
        return calendarEventRepository.findByFamilyIdAndStartTimeBetween(familyId, start, end).map { it.toResponse() }
    }

    @Transactional
    fun createEvent(familyId: Long, user: User, request: CalendarEventRequest): CalendarEventResponse {
        val family = familyRepository.findById(familyId).orElseThrow()
        val event = calendarEventRepository.save(
            CalendarEvent(
                title = request.title,
                description = request.description,
                startTime = request.startTime,
                endTime = request.endTime,
                color = request.color,
                allDay = request.allDay,
                createdBy = user,
                family = family
            )
        )
        return event.toResponse()
    }

    @Transactional
    fun deleteEvent(eventId: Long) {
        calendarEventRepository.deleteById(eventId)
    }
}

@Service
class ListService(
    private val todoListRepository: TodoListRepository,
    private val listItemRepository: ListItemRepository,
    private val familyRepository: FamilyRepository
) {
    fun getLists(familyId: Long): List<TodoListResponse> {
        return todoListRepository.findByFamilyId(familyId).map { list ->
            val total = listItemRepository.countByTodoListIdAndCompleted(list.id, false) +
                    listItemRepository.countByTodoListIdAndCompleted(list.id, true)
            val completed = listItemRepository.countByTodoListIdAndCompleted(list.id, true)
            TodoListResponse(
                list.id, list.name, list.icon, total.toInt(), completed.toInt(),
                list.createdBy?.name, list.createdAt
            )
        }
    }

    @Transactional
    fun createList(familyId: Long, user: User, request: CreateListRequest): TodoListResponse {
        val family = familyRepository.findById(familyId).orElseThrow()
        val list = todoListRepository.save(
            TodoList(name = request.name, icon = request.icon, createdBy = user, family = family)
        )
        return TodoListResponse(list.id, list.name, list.icon, 0, 0, user.name, list.createdAt)
    }

    fun getListItems(listId: Long): List<ListItemResponse> {
        return listItemRepository.findByTodoListId(listId).map { it.toResponse() }
    }

    @Transactional
    fun addItem(listId: Long, request: ListItemRequest): ListItemResponse {
        val list = todoListRepository.findById(listId).orElseThrow()
        val item = listItemRepository.save(
            ListItem(text = request.text, quantity = request.quantity, notes = request.notes, todoList = list)
        )
        return item.toResponse()
    }

    @Transactional
    fun toggleItem(itemId: Long, user: User): ListItemResponse {
        val item = listItemRepository.findById(itemId).orElseThrow()
        item.completed = !item.completed
        item.completedBy = if (item.completed) user else null
        item.completedAt = if (item.completed) LocalDateTime.now() else null
        return listItemRepository.save(item).toResponse()
    }

    @Transactional
    fun deleteItem(itemId: Long) {
        listItemRepository.deleteById(itemId)
    }

    @Transactional
    fun deleteList(listId: Long) {
        todoListRepository.deleteById(listId)
    }
}

@Service
class ExpenseService(
    private val expenseRepository: ExpenseRepository,
    private val savingsGoalRepository: SavingsGoalRepository,
    private val familyRepository: FamilyRepository
) {
    fun getExpenses(familyId: Long): List<ExpenseResponse> {
        return expenseRepository.findByFamilyIdOrderByDateDesc(familyId).map { it.toResponse() }
    }

    fun getMonthlyExpenses(familyId: Long, year: Int, month: Int): List<ExpenseResponse> {
        val start = LocalDate.of(year, month, 1)
        val end = start.plusMonths(1).minusDays(1)
        return expenseRepository.findByFamilyIdAndDateBetween(familyId, start, end).map { it.toResponse() }
    }

    @Transactional
    fun addExpense(familyId: Long, user: User, request: ExpenseRequest): ExpenseResponse {
        val family = familyRepository.findById(familyId).orElseThrow()
        val expense = expenseRepository.save(
            Expense(
                amount = request.amount, description = request.description,
                category = request.category, date = request.date,
                paidBy = user, family = family
            )
        )
        return expense.toResponse()
    }

    @Transactional
    fun deleteExpense(expenseId: Long) {
        expenseRepository.deleteById(expenseId)
    }

    fun getSavingsGoals(familyId: Long): List<SavingsGoalResponse> {
        return savingsGoalRepository.findByFamilyId(familyId).map { it.toResponse() }
    }

    @Transactional
    fun createSavingsGoal(familyId: Long, request: SavingsGoalRequest): SavingsGoalResponse {
        val family = familyRepository.findById(familyId).orElseThrow()
        val goal = savingsGoalRepository.save(
            SavingsGoal(
                name = request.name, targetAmount = request.targetAmount,
                icon = request.icon, family = family
            )
        )
        return goal.toResponse()
    }

    @Transactional
    fun addToSavings(goalId: Long, amount: BigDecimal): SavingsGoalResponse {
        val goal = savingsGoalRepository.findById(goalId).orElseThrow()
        goal.currentAmount = goal.currentAmount.add(amount)
        return savingsGoalRepository.save(goal).toResponse()
    }
}

@Service
class MealPlanService(
    private val mealPlanRepository: MealPlanRepository,
    private val familyRepository: FamilyRepository
) {
    fun getWeeklyPlans(familyId: Long, startDate: LocalDate): List<MealPlanResponse> {
        val endDate = startDate.plusDays(6)
        return mealPlanRepository.findByFamilyIdAndDateBetweenOrderByDateAscMealTypeAsc(familyId, startDate, endDate)
            .map { it.toResponse() }
    }

    fun getAllPlans(familyId: Long): List<MealPlanResponse> {
        return mealPlanRepository.findByFamilyIdOrderByDateDesc(familyId).map { it.toResponse() }
    }

    @Transactional
    fun createMealPlan(familyId: Long, user: User, request: MealPlanRequest): MealPlanResponse {
        val family = familyRepository.findById(familyId).orElseThrow()
        val plan = mealPlanRepository.save(
            MealPlan(
                date = request.date, mealType = request.mealType,
                recipeName = request.recipeName, recipeUrl = request.recipeUrl,
                notes = request.notes, servings = request.servings,
                createdBy = user, family = family
            )
        )
        return plan.toResponse()
    }

    @Transactional
    fun deleteMealPlan(planId: Long) {
        mealPlanRepository.deleteById(planId)
    }
}

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val familyRepository: FamilyRepository
) {
    fun getMessages(familyId: Long): List<MessageResponse> {
        return messageRepository.findTop50ByFamilyIdOrderByTimestampDesc(familyId)
            .reversed()
            .map { it.toResponse() }
    }

    @Transactional
    fun sendMessage(familyId: Long, user: User, request: SendMessageRequest): MessageResponse {
        val family = familyRepository.findById(familyId).orElseThrow()
        val message = messageRepository.save(
            Message(
                content = request.content, mediaUrl = request.mediaUrl,
                messageType = request.messageType, sender = user, family = family
            )
        )
        return message.toResponse()
    }
}

@Service
class NoticeBoardService(
    private val noticeBoardRepository: NoticeBoardRepository,
    private val familyRepository: FamilyRepository
) {
    fun getNotices(familyId: Long): List<NoticeBoardResponse> {
        return noticeBoardRepository.findByFamilyIdOrderByPinnedDescCreatedAtDesc(familyId).map { it.toResponse() }
    }

    @Transactional
    fun createNotice(familyId: Long, user: User, request: NoticeBoardRequest): NoticeBoardResponse {
        val family = familyRepository.findById(familyId).orElseThrow()
        val notice = noticeBoardRepository.save(
            NoticeBoard(
                title = request.title, content = request.content,
                pinned = request.pinned, color = request.color,
                createdBy = user, family = family
            )
        )
        return notice.toResponse()
    }

    @Transactional
    fun togglePin(noticeId: Long): NoticeBoardResponse {
        val notice = noticeBoardRepository.findById(noticeId).orElseThrow()
        notice.pinned = !notice.pinned
        notice.updatedAt = LocalDateTime.now()
        return noticeBoardRepository.save(notice).toResponse()
    }

    @Transactional
    fun deleteNotice(noticeId: Long) {
        noticeBoardRepository.deleteById(noticeId)
    }
}

@Service
class LocationService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun updateLocation(userId: Long, request: LocationUpdateRequest) {
        val user = userRepository.findById(userId).orElseThrow()
        user.lastLatitude = request.latitude
        user.lastLongitude = request.longitude
        user.lastLocationUpdate = LocalDateTime.now()
        userRepository.save(user)
    }

    fun getFamilyLocations(familyId: Long): List<UserResponse> {
        return userRepository.findByFamilyId(familyId)
            .filter { it.lastLatitude != null && it.lastLongitude != null }
            .map { it.toResponse() }
    }
}

@Service
class AdminService(
    private val userRepository: UserRepository,
    private val familyRepository: FamilyRepository,
    private val calendarEventRepository: CalendarEventRepository,
    private val messageRepository: MessageRepository,
    private val expenseRepository: ExpenseRepository
) {
    fun getDashboardStats(): DashboardStats {
        return DashboardStats(
            totalFamilies = familyRepository.count(),
            totalUsers = userRepository.count(),
            activeUsers = userRepository.countByActive(true),
            totalEvents = calendarEventRepository.count(),
            totalMessages = messageRepository.count(),
            totalExpenses = expenseRepository.findAll().sumOf { it.amount }
        )
    }

    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { it.toResponse() }
    }

    fun getUser(userId: Long): UserResponse {
        return userRepository.findById(userId).orElseThrow().toResponse()
    }

    @Transactional
    fun toggleUserActive(userId: Long): UserResponse {
        val user = userRepository.findById(userId).orElseThrow()
        user.active = !user.active
        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun changeUserRole(userId: Long, role: UserRole): UserResponse {
        val user = userRepository.findById(userId).orElseThrow()
        user.role = role
        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun deleteFamily(familyId: Long) {
        val members = userRepository.findByFamilyId(familyId)
        members.forEach { it.family = null; userRepository.save(it) }
        familyRepository.deleteById(familyId)
    }
}

// ── Extension functions for entity-to-DTO conversion ─────

fun User.toResponse() = UserResponse(
    id, name, email, role.name, avatarUrl, family?.id, family?.name,
    active, lastLatitude, lastLongitude, lastLocationUpdate, createdAt
)

fun CalendarEvent.toResponse() = CalendarEventResponse(
    id, title, description, startTime, endTime, color, allDay, createdBy?.name, createdAt
)

fun ListItem.toResponse() = ListItemResponse(
    id, text, completed, quantity, notes, completedBy?.name, completedAt, createdAt
)

fun Expense.toResponse() = ExpenseResponse(
    id, amount, description, category, date, paidBy?.name, createdAt
)

fun SavingsGoal.toResponse(): SavingsGoalResponse {
    val progress = if (targetAmount > BigDecimal.ZERO) {
        currentAmount.multiply(BigDecimal(100)).divide(targetAmount, 0, RoundingMode.HALF_UP).toInt()
    } else 0
    return SavingsGoalResponse(id, name, targetAmount, currentAmount, icon, progress, createdAt)
}

fun MealPlan.toResponse() = MealPlanResponse(
    id, date, mealType.name, recipeName, recipeUrl, notes, servings, createdBy?.name, createdAt
)

fun Message.toResponse() = MessageResponse(
    id, content, mediaUrl, messageType.name, sender?.name ?: "Unknown", sender?.id ?: 0, timestamp
)

fun NoticeBoard.toResponse() = NoticeBoardResponse(
    id, title, content, pinned, color, createdBy?.name, createdAt
)
