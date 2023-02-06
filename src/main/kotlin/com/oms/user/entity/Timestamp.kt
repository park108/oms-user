package com.oms.user.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class Timestamp {

	@CreatedDate
	@Column(nullable = false, updatable = false)
	@ApiModelProperty(notes = "When created record, automatically set by JPA Audit", example = "2023-01-21 21:07:04.994631")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	var createdAt: LocalDateTime = LocalDateTime.MIN

	@CreatedBy
	@Column(nullable = false, updatable = false)
	@ApiModelProperty(notes = "Who created record, automatically set by JPA Audit", example = "park108@gmail.com")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	var createdBy: String = ""

	@LastModifiedDate
	@Column(nullable = false)
	@ApiModelProperty(notes = "When changed record, automatically set by JPA Audit", example = "2023-01-21 21:07:04.994631")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	var changedAt: LocalDateTime = LocalDateTime.MIN

	@LastModifiedBy
	@Column(nullable = false)
	@ApiModelProperty(notes = "Who changed record, automatically set by JPA Audit", example = "park108@gmail.com")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	var changedBy: String = ""
}