package org.rscarela.security.reactive.pendragon.core

import org.rscarela.security.reactive.pendragon.core.credentials.AuthenticatedUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import kotlin.jvm.Throws

class UserAuthentication(
    private val user: AuthenticatedUser?
) : Authentication {

    private var authenticated: Boolean = user != null

    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

    override fun getCredentials(): Any? = null

    override fun getDetails(): Any? = user

    override fun getPrincipal(): Any? = user

    override fun isAuthenticated(): Boolean = authenticated

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }

    override fun getName(): String = user?.getUuid() ?: ""

    /**
     * Convenience method that returns the AuthenticatedUser already parsed
     * to the domain class used as its instance.
     *
     * @return AuthenticatedUser parsed to its actual domain implementation.
     * @param T Actual type of the AuthenticatedUser implementation
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getUser(): T? {
        return user as? T
    }
}