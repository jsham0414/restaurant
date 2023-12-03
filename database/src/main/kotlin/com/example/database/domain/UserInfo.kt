package com.example.database.domain

import com.example.database.security.Encrypt
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "USER_INFO")
class UserInfo(
    @Id
    @Column(name = "user_id")
    val userId: String,

    @Encrypt
    @Column(name = "user_pw")
    val userPw: String,

    @Column(name = "roles")
    var roles: String,

    @Column(name = "phone")
    var phone: String

) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        val collection: MutableCollection<GrantedAuthority?> = arrayListOf()
        collection.add(SimpleGrantedAuthority(roles))
        return collection
    }

    override fun getPassword(): String {
        return userPw
    }

    override fun getUsername(): String {
        return userId
    }

    override fun isAccountNonExpired(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAccountNonLocked(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCredentialsNonExpired(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }
}