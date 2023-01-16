package com.oms.user.controller

import com.oms.user.entity.User
import com.oms.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

private val logger = KotlinLogging.logger {}

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:8081", "https://oms.park108.net"])
@RestController
@RequestMapping("/api")
class UserController(private val repository: UserRepository) {

    private fun hasUser(email: String) = repository.existsByEmail(email)

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

    @GetMapping("/{id}/password/{password}")
    fun checkPassword(@PathVariable id: UUID, @PathVariable password: String) : ResponseEntity<Boolean> {

        val user = repository.findByIdOrNull(id) ?: return notFound().build()

        logger.debug("## User found = ${user.email}")

        val encoder = BCryptPasswordEncoder()
        val matchResult = encoder.matches(password, user.password)

        logger.debug("## Password match result = $matchResult")

        return try {
            when {
                matchResult -> ok(true)
                else -> notFound().build()
            }
        }
        catch(e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }

    @PostMapping("/")
    fun postUser(@RequestBody body: User): ResponseEntity<User> {

        // Encrypt password
        val encoder = BCryptPasswordEncoder()
        body.password = encoder.encode(body.password)

        return try {
            when {
                hasUser(body.email) -> status(HttpStatus.CONFLICT).build()
                else -> {
                    repository.save(body)
                    created(URI.create("/")).build()
                }
            }
        }
        catch(e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }
}