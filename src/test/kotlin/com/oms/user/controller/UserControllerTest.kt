package com.oms.user.controller

import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `GET users 200 ok`() {

        // When
        val result = mockMvc.perform(get("/"))

        // Then
        result.andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.content().string(containsString("Monitor")))
    }
}