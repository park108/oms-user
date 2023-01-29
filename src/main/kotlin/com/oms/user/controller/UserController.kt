package com.oms.user.controller

import com.oms.user.entity.Password
import com.oms.user.entity.User
import com.oms.user.repository.UserRepository
import io.swagger.annotations.ApiOperation
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*

private val logger = KotlinLogging.logger {}

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:8082", "https://oms.park108.net"])
@RestController
@RequestMapping("/api")
class UserController(private val repository: UserRepository) {

    private fun hasUser(email: String?) = repository.existsByEmail(email)

    private val passwordEncoder = BCryptPasswordEncoder()

    private final fun logFunctionStart(methodName: String) {
        logger.info("##################################################")
        logger.info(methodName)
        logger.info("##################################################")
    }

    @GetMapping("/")
    @ApiOperation(value = "User 목록 조회")
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
    @ApiOperation(value = "User 상세 조회")
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
    @ApiOperation(value = "User 생성")
    fun postUser(@RequestBody body: User): ResponseEntity<User> {

        logFunctionStart("postUser")
        logger.info("  user.email = $body.email")
        logger.info("  user.name = $body.name")

        // Encrypt password
        body.password = passwordEncoder.encode(body.password)

        return try {
            when {
                hasUser(body.email) -> status(HttpStatus.CONFLICT).build()
                else -> {
                    ok(repository.save(body))
                }
            }
        }
        catch(e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "User 수정")
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
    @ApiOperation(value = "패스워드 체크")
    fun checkPassword(@PathVariable id: UUID, @RequestBody body: Password) : ResponseEntity<Boolean> {

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

        val currentPassword = body.currentPassword
        if(null == currentPassword || "" == currentPassword) {
            logger.info("  ## currentPassword is required")
            return notFound().build()
        }

        val matchResult = passwordEncoder.matches(currentPassword, user.password)

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
    @ApiOperation(value = "패스워드 변경")
    fun changePassword(@PathVariable id: UUID, @RequestBody body: Password) : ResponseEntity<User> {

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

        val currentPassword = body.currentPassword
        if(null == currentPassword || "" == currentPassword) {
            logger.info("  ## currentPassword is required")
            return notFound().build()
        }

        val isCurrentPasswordMatched = passwordEncoder.matches(currentPassword, user.password)

        if(!isCurrentPasswordMatched) {
            logger.debug("  ## Password NOT matched")
            return notFound().build()
        }

        val newPassword = body.newPassword
        if(null == newPassword || "" == newPassword) {
            logger.info("  ## newPassword is required")
            return notFound().build()
        }

        user.password = passwordEncoder.encode(newPassword)
        user.isPasswordChangeRequired = false

        return try {
            ok().body(repository.save(user))
        }
        catch (e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }

    @PutMapping("/{id}/password/init")
    @ApiOperation(value = "패스워드 초기화")
    fun initPassword(@PathVariable id: UUID, @RequestBody body: Password) : ResponseEntity<User> {

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

        val initPassword = body.initPassword
        if(null == initPassword || "" == initPassword) {
            logger.info("  ## initPassword is required")
            return notFound().build()
        }

        user.password = passwordEncoder.encode(initPassword)
        user.isPasswordChangeRequired = true

        return try {
            ok().body(repository.save(user))
        }
        catch (e: Exception) {
            logger.error { e }
            internalServerError().build()
        }
    }
}