package com.oms.user.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user", indexes = [
    Index(name = "idx__name", columnList = "email")
])
class User (

    @Column(nullable = false)
    @ApiModelProperty(example = "park108@example.com")
    var email: String? = null,

    @Column(nullable = false)
    @ApiModelProperty(example = "Jongkil Park")
    var name: String? = null,

    @Column(nullable = false)
    @ApiModelProperty(example = "1q2w3e")
    var password: String? = null,

    @Column
    @ApiModelProperty(example = false.toString())
    var isPasswordChangeRequired: Boolean = false,

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var id: UUID? = null,
) : Timestamp()