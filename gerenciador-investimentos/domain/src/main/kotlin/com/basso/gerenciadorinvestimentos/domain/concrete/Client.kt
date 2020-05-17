package com.basso.gerenciadorinvestimentos.domain.concrete

import com.basso.gerenciadorinvestimentos.domain.IClient
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity
data class Client (

        @Id
        val email: String,

        val cpf: String,

        val firstName: String,

        val lastName: String?,

        val avatarImage: String? = null,

        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinColumn(name = "user_id", referencedColumnName = "id")
        val user: User,

        @OneToMany(mappedBy = "client",  cascade = [CascadeType.ALL])
        val wallet: List<Wallet> = listOf()

) : BaseEntity(), IClient