package com.oms.user.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user", indexes = [
    Index(name = "idx__name", columnList = "email")
])
class User (

    @Column(nullable = false)
    var email: String? = null,

    @Column(nullable = false)
    var name: String? = null,

    @Column(nullable = false)
    var password: String? = null,

    @Id
    @GeneratedValue
    var id: UUID? = null,
) : Timestamp() {
}