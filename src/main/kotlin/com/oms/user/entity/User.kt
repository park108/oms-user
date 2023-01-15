package com.oms.user.entity

import java.util.*
import javax.persistence.*

@Entity
class User(

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var password: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: UUID = UUID.randomUUID(),
) {
    fun user() = User(email = "example@park108.net", name = "Jongkil Park", password = "password")
}