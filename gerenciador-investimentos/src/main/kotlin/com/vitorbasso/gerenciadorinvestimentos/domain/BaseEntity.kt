package com.vitorbasso.gerenciadorinvestimentos.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity (
        @field:CreationTimestamp
        @Column(updatable = false, nullable = false)
        val dateCreated: LocalDateTime = LocalDateTime.now(),
        @field:UpdateTimestamp
        @Column(nullable = false)
        val dateUpdated: LocalDateTime = LocalDateTime.now()
) : Serializable