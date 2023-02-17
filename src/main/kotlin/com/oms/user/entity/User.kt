package com.oms.user.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user", indexes = [
	Index(name = "idx__name", columnList = "email")
])
data class User(
	@field: Column(nullable = false)
	@field: ApiModelProperty(notes = "e-mail address", example = "park108@example.com", required = true)
	var email: String,

	@field: Column(nullable = false)
	@field: ApiModelProperty(notes = "User's name", example = "Jongkil Park", required = true)
	var name: String,

	@field: Column(nullable = false)
	@field: ApiModelProperty(notes = "User's password", example = "1q2w3e", required = true)
	var password: String,

	@field: Column(nullable = false)
	@field: ApiModelProperty(notes = "Is password change required?", example = "false")
	var isPasswordChangeRequired: Boolean,

	@Id
	@GeneratedValue
	@field: ApiModelProperty(notes = "User's ID, UUID type", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
	@field: JsonProperty(access = JsonProperty.Access.READ_ONLY)
	val id: UUID? = null,
) : Timestamp()