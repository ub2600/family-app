package com.familyapp.controller.api

import com.familyapp.dto.*
import com.familyapp.model.User
import com.familyapp.service.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/family")
class FamilyController(private val familyService: FamilyService) {

    @GetMapping
    fun getFamily(@AuthenticationPrincipal user: User): ResponseEntity<FamilyResponse> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(familyService.getFamily(familyId))
    }

    @GetMapping("/members")
    fun getMembers(@AuthenticationPrincipal user: User): ResponseEntity<List<UserResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(familyService.getFamilyMembers(familyId))
    }
}

@RestController
@RequestMapping("/api/calendar")
class CalendarController(private val calendarService: CalendarService) {

    @GetMapping
    fun getEvents(@AuthenticationPrincipal user: User): ResponseEntity<List<CalendarEventResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(calendarService.getEvents(familyId))
    }

    @GetMapping("/range")
    fun getEventsByRange(
        @AuthenticationPrincipal user: User,
        @RequestParam start: String,
        @RequestParam end: String
    ): ResponseEntity<List<CalendarEventResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(
            calendarService.getEventsBetween(
                familyId,
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
            )
        )
    }

    @PostMapping
    fun createEvent(
        @AuthenticationPrincipal user: User,
        @RequestBody request: CalendarEventRequest
    ): ResponseEntity<CalendarEventResponse> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(calendarService.createEvent(familyId, user, request))
    }

    @DeleteMapping("/{id}")
    fun deleteEvent(@PathVariable id: Long): ResponseEntity<Void> {
        calendarService.deleteEvent(id)
        return ResponseEntity.noContent().build()
    }
}

@RestController
@RequestMapping("/api/lists")
class ListController(private val listService: ListService) {

    @GetMapping
    fun getLists(@AuthenticationPrincipal user: User): ResponseEntity<List<TodoListResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(listService.getLists(familyId))
    }

    @PostMapping
    fun createList(
        @AuthenticationPrincipal user: User,
        @RequestBody request: CreateListRequest
    ): ResponseEntity<TodoListResponse> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(listService.createList(familyId, user, request))
    }

    @GetMapping("/{listId}/items")
    fun getItems(@PathVariable listId: Long): ResponseEntity<List<ListItemResponse>> {
        return ResponseEntity.ok(listService.getListItems(listId))
    }

    @PostMapping("/{listId}/items")
    fun addItem(
        @PathVariable listId: Long,
        @RequestBody request: ListItemRequest
    ): ResponseEntity<ListItemResponse> {
        return ResponseEntity.ok(listService.addItem(listId, request))
    }

    @PutMapping("/items/{itemId}/toggle")
    fun toggleItem(
        @PathVariable itemId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ListItemResponse> {
        return ResponseEntity.ok(listService.toggleItem(itemId, user))
    }

    @DeleteMapping("/items/{itemId}")
    fun deleteItem(@PathVariable itemId: Long): ResponseEntity<Void> {
        listService.deleteItem(itemId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{listId}")
    fun deleteList(@PathVariable listId: Long): ResponseEntity<Void> {
        listService.deleteList(listId)
        return ResponseEntity.noContent().build()
    }
}

@RestController
@RequestMapping("/api/expenses")
class ExpenseController(private val expenseService: ExpenseService) {

    @GetMapping
    fun getExpenses(@AuthenticationPrincipal user: User): ResponseEntity<List<ExpenseResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(expenseService.getExpenses(familyId))
    }

    @GetMapping("/monthly")
    fun getMonthlyExpenses(
        @AuthenticationPrincipal user: User,
        @RequestParam year: Int,
        @RequestParam month: Int
    ): ResponseEntity<List<ExpenseResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(expenseService.getMonthlyExpenses(familyId, year, month))
    }

    @PostMapping
    fun addExpense(
        @AuthenticationPrincipal user: User,
        @RequestBody request: ExpenseRequest
    ): ResponseEntity<ExpenseResponse> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(expenseService.addExpense(familyId, user, request))
    }

    @DeleteMapping("/{id}")
    fun deleteExpense(@PathVariable id: Long): ResponseEntity<Void> {
        expenseService.deleteExpense(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/savings")
    fun getSavingsGoals(@AuthenticationPrincipal user: User): ResponseEntity<List<SavingsGoalResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(expenseService.getSavingsGoals(familyId))
    }

    @PostMapping("/savings")
    fun createSavingsGoal(
        @AuthenticationPrincipal user: User,
        @RequestBody request: SavingsGoalRequest
    ): ResponseEntity<SavingsGoalResponse> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(expenseService.createSavingsGoal(familyId, request))
    }

    @PostMapping("/savings/{goalId}/add")
    fun addToSavings(
        @PathVariable goalId: Long,
        @RequestBody body: Map<String, String>
    ): ResponseEntity<SavingsGoalResponse> {
        val amount = body["amount"]?.toBigDecimalOrNull() ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(expenseService.addToSavings(goalId, amount))
    }
}

@RestController
@RequestMapping("/api/meals")
class MealPlanController(private val mealPlanService: MealPlanService) {

    @GetMapping
    fun getPlans(@AuthenticationPrincipal user: User): ResponseEntity<List<MealPlanResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(mealPlanService.getAllPlans(familyId))
    }

    @GetMapping("/week")
    fun getWeeklyPlans(
        @AuthenticationPrincipal user: User,
        @RequestParam startDate: String
    ): ResponseEntity<List<MealPlanResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(mealPlanService.getWeeklyPlans(familyId, LocalDate.parse(startDate)))
    }

    @PostMapping
    fun createPlan(
        @AuthenticationPrincipal user: User,
        @RequestBody request: MealPlanRequest
    ): ResponseEntity<MealPlanResponse> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(mealPlanService.createMealPlan(familyId, user, request))
    }

    @DeleteMapping("/{id}")
    fun deletePlan(@PathVariable id: Long): ResponseEntity<Void> {
        mealPlanService.deleteMealPlan(id)
        return ResponseEntity.noContent().build()
    }
}

@RestController
@RequestMapping("/api/messages")
class MessageController(private val messageService: MessageService) {

    @GetMapping
    fun getMessages(@AuthenticationPrincipal user: User): ResponseEntity<List<MessageResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(messageService.getMessages(familyId))
    }

    @PostMapping
    fun sendMessage(
        @AuthenticationPrincipal user: User,
        @RequestBody request: SendMessageRequest
    ): ResponseEntity<MessageResponse> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(messageService.sendMessage(familyId, user, request))
    }
}

@RestController
@RequestMapping("/api/noticeboard")
class NoticeBoardController(private val noticeBoardService: NoticeBoardService) {

    @GetMapping
    fun getNotices(@AuthenticationPrincipal user: User): ResponseEntity<List<NoticeBoardResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(noticeBoardService.getNotices(familyId))
    }

    @PostMapping
    fun createNotice(
        @AuthenticationPrincipal user: User,
        @RequestBody request: NoticeBoardRequest
    ): ResponseEntity<NoticeBoardResponse> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(noticeBoardService.createNotice(familyId, user, request))
    }

    @PutMapping("/{id}/pin")
    fun togglePin(@PathVariable id: Long): ResponseEntity<NoticeBoardResponse> {
        return ResponseEntity.ok(noticeBoardService.togglePin(id))
    }

    @DeleteMapping("/{id}")
    fun deleteNotice(@PathVariable id: Long): ResponseEntity<Void> {
        noticeBoardService.deleteNotice(id)
        return ResponseEntity.noContent().build()
    }
}

@RestController
@RequestMapping("/api/location")
class LocationController(private val locationService: LocationService) {

    @PostMapping
    fun updateLocation(
        @AuthenticationPrincipal user: User,
        @RequestBody request: LocationUpdateRequest
    ): ResponseEntity<Void> {
        locationService.updateLocation(user.id, request)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/family")
    fun getFamilyLocations(@AuthenticationPrincipal user: User): ResponseEntity<List<UserResponse>> {
        val familyId = user.family?.id ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(locationService.getFamilyLocations(familyId))
    }
}
