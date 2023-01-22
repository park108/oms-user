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

    private final fun logFunctionStart(methodName: String) {
        logger.info("##################################################")
        logger.info(methodName)
        logger.info("##################################################")
    }

    @GetMapping("/")
    fun getUsers() : ResponseEntity<Iterable<User>> {

        logFunctionStart("getUsers")

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

        logFunctionStart("getUser")
        logger.info("  user.id = $id")

        return try {
            when(val user = repository.findByIdOrNull(id)) {
                null -> {
                    logger.info("  ## User NOT found")
                    notFound().build()
                }
                else -> {
                    logger.debug("  ## User found = ${user.email}")
                    ok(user)
                }
            }
        }
        catch(e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }

    @PostMapping("/")
    fun postUser(@RequestBody body: User): ResponseEntity<User> {

        logFunctionStart("postUser")
        logger.info("  user.email = $body.email")
        logger.info("  user.name = $body.name")

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

        logFunctionStart("putUser")
        logger.info("  user.id = $id")
        logger.info("  user.email = $body.email")
        logger.info("  user.name = $body.name")

        val user = repository.findByIdOrNull(id)

        when(user) {
            null -> {
                logger.info("  ## User NOT found")
                return notFound().build()
            }
            else -> logger.debug("  ## User found = ${user.email}")
        }

        // Change editable values
        user.name = body.name
        user.email = body.email

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

        logFunctionStart("checkPassword")
        logger.info("  user.id = $id")

        val user = repository.findByIdOrNull(id)

        when(user) {
            null -> {
                logger.info("  ## User NOT found")
                return notFound().build()
            }
            else -> logger.debug("  ## User found = ${user.email}")
        }

        val password = body["password"] as String
        val encoder = BCryptPasswordEncoder()
        val matchResult = encoder.matches(password, user.password)

        when {
            matchResult -> logger.debug("  ## Password matched")
            else -> logger.debug("  ## Password NOT matched")
        }

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

        logFunctionStart("changePassword")
        logger.info("  user.id = $id")

        val user = repository.findByIdOrNull(id)

        when(user) {
            null -> {
                logger.info("  ## User NOT found")
                return notFound().build()
            }
            else -> logger.debug("  ## User found = ${user.email}")
        }

        val currentPassword = body["currentPassword"] as String
        val encoder = BCryptPasswordEncoder()
        val isCurrentPasswordMatched = encoder.matches(currentPassword, user.password)

        when {
            isCurrentPasswordMatched -> {
                logger.debug("  ## Password matched")
                return notFound().build()
            }
            else -> logger.debug("  ## Password NOT matched")
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

    @PutMapping("/{id}/password/init")
    fun initPassword(@PathVariable id: UUID, @RequestBody body: Map<String, Any>) : ResponseEntity<User> {

        logFunctionStart("initPassword")
        logger.info("  user.id = $id")

        val user = repository.findByIdOrNull(id)

        when(user) {
            null -> {
                logger.info("  ## User NOT found")
                return notFound().build()
            }
            else -> logger.debug("  ## User found = ${user.email}")
        }

        val initPassword = body["initPassword"] as String
        val encoder = BCryptPasswordEncoder()

        user.password = encoder.encode(initPassword)

        return try {
            ok().body(repository.save(user))
        }
        catch (e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }
}