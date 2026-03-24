package com.familyapp.controller.api

import com.familyapp.dto.*
import com.familyapp.model.User
import com.familyapp.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return try {
            ResponseEntity.ok(authService.register(request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            ResponseEntity.ok(authService.login(request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).build()
        }
    }
}
