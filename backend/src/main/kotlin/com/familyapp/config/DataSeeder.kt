package com.familyapp.config

import com.familyapp.model.Family
import com.familyapp.model.User
import com.familyapp.model.UserRole
import com.familyapp.repository.FamilyRepository
import com.familyapp.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DataSeeder {

    @Bean
    fun seedData(
        userRepository: UserRepository,
        familyRepository: FamilyRepository,
        passwordEncoder: PasswordEncoder
    ) = CommandLineRunner {
        // Create admin user if not exists
        if (userRepository.findByEmail("admin@familyapp.com") == null) {
            val adminFamily = familyRepository.save(
                Family(
                    name = "Admin Family",
                    inviteCode = "ADMIN001"
                )
            )

            userRepository.save(
                User(
                    name = "System Admin",
                    email = "admin@familyapp.com",
                    password = passwordEncoder.encode("admin123"),
                    role = UserRole.ADMIN,
                    family = adminFamily
                )
            )

            println("✅ Default admin created: admin@familyapp.com / admin123")
        }
    }
}
