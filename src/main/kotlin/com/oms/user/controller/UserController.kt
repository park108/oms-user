package com.oms.user.controller

import com.oms.user.entity.Password
import com.oms.user.entity.User
import com.oms.user.repository.UserRepository
import io.swagger.annotations.ApiOperation
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = ["http://localhost:8080", "http://localhost:8082", "https://oms.park108.net"])
@RestController
@RequestMapping("/api")
class UserController(private val repository: UserRepository) {

    private fun hasUser(email: String?) = repository.existsByEmail(email)

    companion object {
        private val passwordEncoder = BCryptPasswordEncoder()
    }

    @GetMapping("/")
    @ApiOperation(value = "User 목록 조회")
    fun getUsers() : ResponseEntity<Iterable<User>> {

        return try {
            val users = repository.findAll()
            when(users.none()) {
                true -> notFound()
                        .header("oms-result-message", "User NOT found")
                        .build()
                false -> ok()
                        .header("oms-result-message", "${users.count()} user(s) found")
                        .body(users)
            }
        }
        catch(e: Exception) {
            internalServerError()
                    .header("oms-result-message", "Exception thrown")
                    .build()
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "User 상세 조회")
    fun getUser(@PathVariable id: UUID) : ResponseEntity<User> {

        return try {
            when(val user = repository.findByIdOrNull(id)) {
                null -> notFound()
                        .header("oms-result-message", "User NOT found")
                        .build()
                else -> ok()
                        .header("oms-result-message", "User '${user.name}' found")
                        .body(user)
            }
        }
        catch(e: Exception) {
            internalServerError()
                    .header("oms-result-message", "Exception thrown")
                    .build()
        }
    }

    @PostMapping("/")
    @ApiOperation(value = "User 생성")
    fun postUser(@RequestBody user: User): ResponseEntity<User> {

        // Encrypt password
        user.password = passwordEncoder.encode(user.password)

        return try {
            when {
                hasUser(user.email) -> status(HttpStatus.CONFLICT)
                        .header("oms-result-message", "User already exists")
                        .build()
                else -> status(HttpStatus.CREATED)
                        .header("oms-result-message", "User ${user.name} created")
                        .body(repository.save(user))
            }
        }
        catch(e: Exception) {
            internalServerError()
                    .header("oms-result-message", "Exception thrown")
                    .build()
        }
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "User 수정")
    fun putUser(@PathVariable id: UUID, @RequestBody body: User): ResponseEntity<User> {

        val user = repository.findByIdOrNull(id)
                ?: return notFound()
                        .header("oms-result-message", "User NOT found")
                        .build()

        // Change editable values
        user.name = body.name
        user.email = body.email

        return try {
            ok().header("oms-result-message", "User {$id} changed")
                    .body(repository.save(user))
        }
        catch(e: Exception) {
            internalServerError()
                    .header("oms-result-message", "Exception thrown")
                    .build()
        }
    }

    @PostMapping("/{id}/password")
    @ApiOperation(value = "패스워드 체크")
    fun checkPassword(@PathVariable id: UUID, @RequestBody body: Password) : ResponseEntity<Boolean> {

        val user = repository.findByIdOrNull(id)
                ?: return notFound()
                        .header("oms-result-message", "User NOT found")
                        .build()

        val currentPassword = body.currentPassword
                ?: return badRequest()
                        .header("oms-result-message", "currentPassword value required")
                        .build()

        return when(passwordEncoder.matches(currentPassword, user.password)) {
            false -> notFound()
                    .header("oms-result-message", "currentPassword NOT matched")
                    .build()
            true -> ok()
                    .header("oms-result-message", "Password matched")
                    .body(true)
        }
    }

    @PutMapping("/{id}/password")
    @ApiOperation(value = "패스워드 변경")
    fun changePassword(@PathVariable id: UUID, @RequestBody body: Password) : ResponseEntity<User> {

        val user = repository.findByIdOrNull(id)
                ?: return notFound()
                        .header("oms-result-message", "User NOT found")
                        .build()

        val currentPassword = body.currentPassword
                ?: return badRequest()
                    .header("oms-result-message", "currentPassword value required")
                    .build()

        val isCurrentPasswordMatched = passwordEncoder.matches(currentPassword, user.password)
        if(!isCurrentPasswordMatched) return notFound()
                .header("oms-result-message", "currentPassword NOT matched")
                .build()

        val newPassword = body.newPassword
                ?: return badRequest()
                    .header("oms-result-message", "newPassword value is required")
                    .build()

        // Change editable values
        user.password = passwordEncoder.encode(newPassword)
        user.isPasswordChangeRequired = false

        return try {
            ok().header("oms-result-message", "Password changed")
                    .body(repository.save(user))
        }
        catch(e: Exception) {
            internalServerError()
                    .header("oms-result-message", "Exception thrown")
                    .build()
        }
    }

    @PutMapping("/{id}/password/init")
    @ApiOperation(value = "패스워드 초기화")
    fun initPassword(@PathVariable id: UUID, @RequestBody body: Password) : ResponseEntity<User> {

        val user = repository.findByIdOrNull(id)
                ?: return notFound()
                        .header("oms-result-message", "User NOT found")
                        .build()

        val initPassword = body.initPassword
                ?: return badRequest()
                        .header("oms-result-message", "initPassword value required")
                        .build()

        // Change editable values
        user.password = passwordEncoder.encode(initPassword)
        user.isPasswordChangeRequired = true

        return try {
            ok()
                    .header("oms-result-message", "Password initialized")
                    .body(repository.save(user))
        }
        catch(e: Exception) {
            internalServerError()
                    .header("oms-result-message", "Exception thrown")
                    .build()
        }
    }
}