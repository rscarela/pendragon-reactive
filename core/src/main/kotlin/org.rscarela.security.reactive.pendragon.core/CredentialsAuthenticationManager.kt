package org.rscarela.security.reactive.pendragon.core

import org.rscarela.security.reactive.pendragon.core.credentials.AuthenticatedUser
import org.rscarela.security.reactive.pendragon.core.credentials.AuthenticatedUserProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Authentication provider based on user's username and password.
 *
 * This is the default behavior for extracting the UserAuthentication
 * based on Spring Security Context, by using the AuthenticatedUserProvider
 * implementation provided by the application domain.
 *
 * @see AuthenticatedUserProvider
 * @see AuthenticatedUser
 * @see UserAuthentication
 * @since 1.0.0
 * @author Renan Scarela
 */
@Component
class CredentialsAuthenticationManager(
    private val authenticatedUserProvider: AuthenticatedUserProvider<AuthenticatedUser>
) : ReactiveAuthenticationManager {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Mono<Authentication?>? {
        val username = authentication.name
        val password = authentication.credentials.toString()

        val user = authenticatedUserProvider.findByCredentials(username, password)

        return Mono.just(user).flatMap {
            if (user.isEmpty) throw BadCredentialsException("Unable to find user with provided credentials")
            else Mono.just(UserAuthentication(user.get()))
        }
    }

    fun supports(authentication: Class<*>): Boolean {
        return authentication == UsernamePasswordAuthenticationToken::class.java
    }
}
