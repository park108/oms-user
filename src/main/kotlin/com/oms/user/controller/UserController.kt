package com.oms.user.controller

import com.oms.user.entity.User
import com.oms.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

private val logger = KotlinLogging.logger {}

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:8081", "https://oms.park108.net"])
@RestController
@RequestMapping("/api")
class UserController(private val repository: UserRepository) {

    @GetMapping("/")
    fun getUsers() : ResponseEntity<Iterable<User>> {
        return try {
            val users = repository.findAll()
            when(users.none()) {
                true -> notFound().build()
                false -> ok(users)
            }
        }
        catch(e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID) : ResponseEntity<User> {
        return try {
            when(val user = repository.findByIdOrNull(id)) {
                null -> notFound().build()
                else -> ok(user)
            }
        }
        catch(e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }
}