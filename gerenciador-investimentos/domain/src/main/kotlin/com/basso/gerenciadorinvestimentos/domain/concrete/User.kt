package com.basso.gerenciadorinvestimentos.domain.concrete

import com.basso.gerenciadorinvestimentos.domain.BaseEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        val password: String = "",

        @OneToOne(mappedBy = "user")
        val client: Client? = null

) : BaseEntity()