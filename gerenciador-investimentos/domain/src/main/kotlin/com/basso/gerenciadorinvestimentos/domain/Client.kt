package com.basso.gerenciadorinvestimentos.domain

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity
data class Client (

        @Id
        val email: String,

        val cpf: String,

        val name: String,

        val avatarImage: String,

        @OneToOne(mappedBy = "client")
        val user: User,

        @OneToMany(mappedBy = "client",  cascade = [CascadeType.ALL])
        val wallet: MutableList<Wallet> = mutableListOf()
)