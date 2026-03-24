package com.familyapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FamilyAppApplication

fun main(args: Array<String>) {
    runApplication<FamilyAppApplication>(*args)
}
