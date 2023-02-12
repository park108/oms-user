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
		val user = User()

		user.email = "delete_soon@example.com"
		user.name = "Delete Soon"
		user.password = "1q2w3e"

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
		val user = User()

		user.email = "origin@gmail.com"
		user.name = "Origin"
		user.password = "1q2w3e"

		val createResult = userController.postUser(user)
		assertEquals("201 CREATED", createResult.statusCode.toString(), "Created")

		// Create duplicated user
		val duplicatedUser = User()

		duplicatedUser.email = "origin@gmail.com"
		duplicatedUser.name = "email duplicated"
		duplicatedUser.password = "1q2w3e4r"

		val duplicateResult = userController.postUser(user)
		assertEquals("409 CONFLICT", duplicateResult.statusCode.toString(), "Conflict")

		// Clean up - delete original user
		val deleteResult = userController.deleteUser(createResult.body?.id!!)
		assertEquals("200 OK", deleteResult.statusCode.toString(), "Deleted")
	}
}