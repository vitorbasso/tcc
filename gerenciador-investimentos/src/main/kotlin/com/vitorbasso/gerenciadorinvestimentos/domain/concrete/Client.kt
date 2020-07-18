package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Client (

        @Id
        val cpf: String = "",

        val email: String = "",

        val password: String = "",

        val firstName: String = "",

        val lastName: String? = null,

        val avatarImage: String? = null,

        @OneToMany(mappedBy = "client",  cascade = [CascadeType.ALL])
        val wallet: List<Wallet> = listOf()

) : BaseEntity(), IClient