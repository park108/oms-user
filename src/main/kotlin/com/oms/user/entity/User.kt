package com.oms.user.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user")
class User(

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var password: String,

    @Id @GeneratedValue
    var id: UUID? = null,
)