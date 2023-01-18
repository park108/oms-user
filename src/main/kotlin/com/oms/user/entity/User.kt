package com.oms.user.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user", indexes = [
    Index(name = "idx__name", columnList = "email")
])
class User (

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var password: String,

    @Id
    @GeneratedValue
    var id: UUID? = null,
) : BaseEntity() {
}