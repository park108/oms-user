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
    fun `create user but already exists`() {
        val user = User()

        user.email = "park108@gmail.com"
        user.name = "Jongkil Park"
        user.password = "1q2w3e"

        val result = userController.postUser(user)
        assertEquals("409 CONFLICT", result.statusCode.toString(), "Conflict")
    }
}