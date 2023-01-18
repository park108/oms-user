package com.oms.user.entity

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
abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.MIN

    @CreatedBy
    @Column(nullable = false, updatable = false)
    var createdBy: String = ""

    @LastModifiedDate
    @Column(nullable = false)
    var changedAt: LocalDateTime = LocalDateTime.MIN

    @LastModifiedBy
    @Column(nullable = false)
    var changedBy: String = ""
}