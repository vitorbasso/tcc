package com.vitorbasso.gerenciadorinvestimentos.domain.concrete

import com.vitorbasso.gerenciadorinvestimentos.domain.BaseEntity
import com.vitorbasso.gerenciadorinvestimentos.domain.IClient
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
data class Client(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val email: String = "",

    private val password: String = "",

    val name: String = "",

    @OneToMany(mappedBy = "client", cascade = [CascadeType.ALL])
    val wallets: List<Wallet> = listOf()

) : BaseEntity(), IClient, UserDetails {

    override fun getUsername() = this.email

    override fun getPassword() = this.password

    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun isEnabled() = true

    override fun isCredentialsNonExpired() = true

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true
}