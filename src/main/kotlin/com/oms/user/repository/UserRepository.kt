package com.oms.user.repository

import com.oms.user.entity.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, UUID> {
    fun existsByEmail(email: String): Boolean
    fun existsByIdAndPassword(id: UUID, password: String): Boolean
}