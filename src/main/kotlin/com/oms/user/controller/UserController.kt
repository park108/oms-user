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

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:8082", "https://oms.park108.net"])
@RestController
@RequestMapping("/api")
class UserController(private val repository: UserRepository) {

    private fun hasUser(email: String?) = repository.existsByEmail(email)

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

    @PutMapping("/{id}")
    fun putUser(@PathVariable id: UUID, @RequestBody body: User): ResponseEntity<User> {

        val user = repository.findByIdOrNull(id)

        if(null == user) {
            logger.debug("## User not found = $id")
            return notFound().build()
        }

        // Change editable values
        user.name = body.name
        user.email = body.email
        user.changedBy = body.changedBy

        logger.debug("## User id    = ${user.id}")
        logger.debug("## User name  = ${user.name}")
        logger.debug("## Email = ${user.email}")
        logger.debug("## Changed by = ${user.changedBy}")

        return try {
            ok().body(repository.save(user))
        }
        catch (e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }

    @PostMapping("/{id}/password")
    fun checkPassword(@PathVariable id: UUID, @RequestBody body: Map<String, Any>) : ResponseEntity<Boolean> {

        val user = repository.findByIdOrNull(id)

        if(null == user) {
            logger.debug("## User not found = $id")
            return notFound().build()
        }

        logger.debug("## User found = ${user.email}")

        val password = body["password"] as String
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

    @PutMapping("/{id}/password")
    fun changePassword(@PathVariable id: UUID, @RequestBody body: Map<String, Any>) : ResponseEntity<User> {

        val user = repository.findByIdOrNull(id)

        if(null == user) {
            logger.debug("## User not found = $id")
            return notFound().build()
        }

        logger.debug("## User found = ${user.email}")

        val currentPassword = body["currentPassword"] as String
        val encoder = BCryptPasswordEncoder()
        val isCurrentPasswordMatched = encoder.matches(currentPassword, user.password)

        if(!isCurrentPasswordMatched) {
            logger.debug("## Current password not matched")
            return notFound().build()
        }

        val newPassword = body["newPassword"] as String
        user.password = encoder.encode(newPassword)

        return try {
            ok().body(repository.save(user))
        }
        catch (e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }
}