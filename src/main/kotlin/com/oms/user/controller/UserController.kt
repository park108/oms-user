package com.oms.user.controller

import com.oms.user.entity.Password
import com.oms.user.entity.User
import com.oms.user.repository.UserRepository
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*

@CrossOrigin(origins = ["http://localhost:8080", "https://oms.park108.net"])
@RestController
@RequestMapping("/api")
class UserController(private val repository: UserRepository) {

	private fun hasUser(email: String?) = repository.existsByEmail(email)

	companion object {
		private val passwordEncoder = BCryptPasswordEncoder()
	}

	@GetMapping("/")
	@ApiOperation(value = "Get user list", notes = "Return user list")
	@ApiResponses(
		ApiResponse(code = 404, message = "User not found"),
		ApiResponse(code = 200, message = "Retrieve one or more users successfully")
	)
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
				.header("oms-result-message", e.message)
				.build()
		}
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "Get a user", notes = "Return a user")
	@ApiResponses(
		ApiResponse(code = 404, message = "User not found"),
		ApiResponse(code = 200, message = "Retrieve a user successfully")
	)
	fun getUser(
		@PathVariable @ApiParam(name = "id", value = "User ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") id: UUID
	) : ResponseEntity<User> {

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
				.header("oms-result-message", e.message)
				.build()
		}
	}

	@PostMapping("/")
	@ApiOperation(value = "Create a user", notes = "Insert a user")
	@ApiResponses(
		ApiResponse(code = 409, message = "User already exists"),
		ApiResponse(code = 201, message = "Post a user successfully")
	)
	fun postUser(
		@RequestBody user: User
	): ResponseEntity<User> {

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
				.header("oms-result-message", e.message)
				.build()
		}
	}

	@PutMapping("/{id}")
	@ApiOperation(value = "Change a user", notes = "Update a user")
	@ApiResponses(
		ApiResponse(code = 404, message = "User not found"),
		ApiResponse(code = 200, message = "Put a user successfully")
	)
	fun putUser(
		@PathVariable @ApiParam(name = "id", value = "User ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") id: UUID
		, @RequestBody body: User
	): ResponseEntity<User> {

		val user = repository.findByIdOrNull(id)
			?: return notFound()
				.header("oms-result-message", "User NOT found")
				.build()

		// Change editable values
		if(null != body.name) user.name = body.name
		if(null != body.email) user.email = body.email

		return try {
			ok().header("oms-result-message", "User {$id} changed")
				.body(repository.save(user))
		}
		catch(e: Exception) {
			internalServerError()
				.header("oms-result-message", e.message)
				.build()
		}
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "Delete a user", notes = "Delete a user")
	@ApiResponses(
		ApiResponse(code = 404, message = "User not found"),
		ApiResponse(code = 200, message = "Delete a user successfully")
	)
	fun deleteUser(
		@PathVariable @ApiParam(name = "id", value = "User ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") id: UUID
	): ResponseEntity<User> {

		val user = repository.findByIdOrNull(id)
			?: return notFound()
				.header("oms-result-message", "User NOT found")
				.build()

		return try {
			repository.delete(user)
			ok().header("oms-result-message", "User {$id} deleted").body(user)
		}
		catch(e: Exception) {
			internalServerError()
				.header("oms-result-message", e.message)
				.build()
		}
	}

	@PostMapping("/{id}/password")
	@ApiOperation(value = "Check user's password", notes = "Return currentPassword validation result")
	@ApiResponses(
		ApiResponse(code = 404, message = "User not found"),
		ApiResponse(code = 400, message = "currentPassword not entered"),
		ApiResponse(code = 403, message = "currentPassword not matched"),
		ApiResponse(code = 200, message = "currentPassword matched")
	)
	fun checkPassword(
		@PathVariable @ApiParam(name = "id", value = "User ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") id: UUID
		, @RequestBody body: Password
	) : ResponseEntity<Boolean> {

		val user = repository.findByIdOrNull(id)
			?: return notFound()
				.header("oms-result-message", "User NOT found")
				.build()

		val currentPassword = body.currentPassword
			?: return badRequest()
				.header("oms-result-message", "currentPassword value required")
				.build()

		return when(passwordEncoder.matches(currentPassword, user.password)) {
			false -> status(HttpStatus.FORBIDDEN)
				.header("oms-result-message", "currentPassword NOT matched")
				.build()
			true -> ok()
				.header("oms-result-message", "Password matched")
				.body(true)
		}
	}

	@PutMapping("/{id}/password")
	@ApiOperation(value = "Change user's password", notes = "Check currentPassword and replace it newPassword")
	@ApiResponses(
		ApiResponse(code = 404, message = "User not found"),
		ApiResponse(code = 400, message = "currentPassword or newPassword not entered"),
		ApiResponse(code = 403, message = "currentPassword not matched"),
		ApiResponse(code = 200, message = "currentPassword changed")
	)
	fun changePassword(
		@PathVariable @ApiParam(name = "id", value = "User ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") id: UUID
		, @RequestBody body: Password
	) : ResponseEntity<User> {

		val user = repository.findByIdOrNull(id)
			?: return notFound()
				.header("oms-result-message", "User NOT found")
				.build()

		val currentPassword = body.currentPassword
			?: return badRequest()
				.header("oms-result-message", "currentPassword value required")
				.build()

		val isCurrentPasswordMatched = passwordEncoder.matches(currentPassword, user.password)
		if(!isCurrentPasswordMatched) return status(HttpStatus.FORBIDDEN)
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
				.header("oms-result-message", e.message)
				.build()
		}
	}

	@PutMapping("/{id}/password/init")
	@ApiOperation(value = "Initialize user's password", notes = "Change user's password with the initPassword")
	@ApiResponses(
		ApiResponse(code = 404, message = "User not found"),
		ApiResponse(code = 400, message = "initPassword not entered"),
		ApiResponse(code = 200, message = "currentPassword initialized")
	)
	fun initPassword(
		@PathVariable @ApiParam(name = "id", value = "User ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") id: UUID
		, @RequestBody body: Password
	) : ResponseEntity<User> {

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
				.header("oms-result-message", e.message)
				.build()
		}
	}
}