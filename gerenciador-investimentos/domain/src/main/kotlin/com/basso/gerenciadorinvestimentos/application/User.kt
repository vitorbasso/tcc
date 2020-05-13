package com.basso.gerenciadorinvestimentos.application

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
data class User(

        val password: String,

        @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinColumn(name = "client_email", referencedColumnName = "email")
        val client: Client

)