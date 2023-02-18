package com.oms.user.controller

import com.oms.user.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@Transactional
class UserControllerTest(
	@Autowired val userController: UserController
//    , @Autowired val userRepository: UserRepository
) {

	@Test
	fun `get users`() {
		val result = userController.getUsers()
		assertEquals("200 OK", result.statusCode.toString(), "OK")
	}

	@Test
	fun `get user`() {
		val result = userController.getUsers()

		result.body?.forEach {
			val user = it.id?.let { it1 -> userController.getUser(it1) }
			assertNotNull(user)
		}
	}

	@Test
	fun `create, change and delete user` () {

		// Create new user
		val user = User(
			email = "delete_soon@example.com",
			name = "Delete Soon",
			password = "1q2w3e",
			isPasswordChangeRequired = false
		)

		val createResult = userController.postUser(user)
		assertEquals("201 CREATED", createResult.statusCode.toString(), "Created")

		// Get created user
		val createdUser = userController.getUser(createResult.body?.id!!).body
		assertNotNull(createdUser)

		// Change created user
		val changedName = "Changed"
		createdUser.name = changedName

		val changedResult = userController.putUser(createdUser.id!!, createdUser)
		assertEquals("200 OK", changedResult.statusCode.toString(), "Changed")

		// Get changed user
		val changedUser = userController.getUser(changedResult.body?.id!!).body
		assertEquals(changedUser!!.name, changedName)

		// Clean up - delete changed user
		val deleteResult = userController.deleteUser(changedUser.id!!)
		assertEquals("200 OK", deleteResult.statusCode.toString(), "Deleted")
	}

	@Test
	fun `create user but already exists`() {

		// Create new user
		val user = User(
			email = "origin@gmail.com",
			name = "Origin",
			password = "1q2w3e",
			isPasswordChangeRequired = false
		)

		val createResult = userController.postUser(user)
		assertEquals("201 CREATED", createResult.statusCode.toString(), "Created")

		// Create duplicated user
		val duplicatedUser = User(
			email = "origin@gmail.com",
			name = "email duplicated",
			password = "1q2w3e4r",
			isPasswordChangeRequired = false,
		)

		val duplicateResult = userController.postUser(duplicatedUser)
		assertEquals("409 CONFLICT", duplicateResult.statusCode.toString(), "Conflict")

		// Clean up - delete original user
		val deleteResult = userController.deleteUser(createResult.body?.id!!)
		assertEquals("200 OK", deleteResult.statusCode.toString(), "Deleted")
	}
}