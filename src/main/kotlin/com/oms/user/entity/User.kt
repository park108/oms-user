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
	@ApiModelProperty(notes = "e-mail address", example = "park108@example.com", required = true)
	var email: String? = null,

	@Column(nullable = false)
	@ApiModelProperty(notes = "User's name", example = "Jongkil Park", required = true)
	var name: String? = null,

	@Column(nullable = false)
	@ApiModelProperty(notes = "User's password", example = "1q2w3e", required = true)
	var password: String? = null,

	@Column(nullable = false)
	@ApiModelProperty(notes = "Is password change required?", example = false.toString())
	var isPasswordChangeRequired: Boolean = false,

	@Id
	@GeneratedValue
	@ApiModelProperty(notes = "User's ID, UUID type", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	var id: UUID? = null,
) : Timestamp()