package com.familyapp.controller.admin

import com.familyapp.model.UserRole
import com.familyapp.service.AdminService
import com.familyapp.service.FamilyService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin")
class AdminDashboardController(
    private val adminService: AdminService,
    private val familyService: FamilyService
) {
    @GetMapping("/login")
    fun loginPage(): String = "admin/login"

    @GetMapping("/dashboard")
    fun dashboard(model: Model): String {
        model.addAttribute("stats", adminService.getDashboardStats())
        model.addAttribute("recentUsers", adminService.getAllUsers().take(10))
        return "admin/dashboard"
    }

    @GetMapping("/users")
    fun users(model: Model): String {
        model.addAttribute("users", adminService.getAllUsers())
        return "admin/users"
    }

    @GetMapping("/users/{id}")
    fun userDetail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("user", adminService.getUser(id))
        model.addAttribute("roles", UserRole.values())
        return "admin/user-detail"
    }

    @PostMapping("/users/{id}/toggle")
    fun toggleUser(@PathVariable id: Long): String {
        adminService.toggleUserActive(id)
        return "redirect:/admin/users"
    }

    @PostMapping("/users/{id}/role")
    fun changeRole(@PathVariable id: Long, @RequestParam role: String): String {
        adminService.changeUserRole(id, UserRole.valueOf(role))
        return "redirect:/admin/users/$id"
    }

    @GetMapping("/families")
    fun families(model: Model): String {
        model.addAttribute("families", familyService.getAllFamilies())
        return "admin/families"
    }

    @GetMapping("/families/{id}")
    fun familyDetail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("family", familyService.getFamily(id))
        model.addAttribute("members", familyService.getFamilyMembers(id))
        return "admin/family-detail"
    }

    @PostMapping("/families/{id}/delete")
    fun deleteFamily(@PathVariable id: Long): String {
        adminService.deleteFamily(id)
        return "redirect:/admin/families"
    }

    @GetMapping("")
    fun adminRoot(): String = "redirect:/admin/dashboard"
}
