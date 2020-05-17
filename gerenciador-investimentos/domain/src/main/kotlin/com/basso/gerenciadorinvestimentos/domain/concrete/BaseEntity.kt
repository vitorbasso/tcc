package com.basso.gerenciadorinvestimentos.domain.concrete

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity (
        @field:CreationTimestamp
        @Column(updatable = false)
        val dateCreated: LocalDateTime = LocalDateTime.now(),
        @field:UpdateTimestamp
        val dateUpdated: LocalDateTime = LocalDateTime.now()
) : Serializable