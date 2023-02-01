package com.oms.user.entity

import com.fasterxml.jackson.annotation.JsonProperty
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var createdAt: LocalDateTime = LocalDateTime.MIN

    @CreatedBy
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var createdBy: String = ""

    @LastModifiedDate
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var changedAt: LocalDateTime = LocalDateTime.MIN

    @LastModifiedBy
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var changedBy: String = ""
}