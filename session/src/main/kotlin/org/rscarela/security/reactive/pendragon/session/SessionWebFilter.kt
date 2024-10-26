package org.rscarela.security.reactive.pendragon.session

import com.fasterxml.jackson.databind.ObjectMapper
import org.rscarela.security.reactive.pendragon.core.CredentialsAuthenticationConverter
import org.rscarela.security.reactive.pendragon.core.credentials.CredentialsTypeProvider
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SessionWebFilter(
    private val authenticationManager: ReactiveAuthenticationManager,
    private val objectMapper: ObjectMapper,
    private val credentialsTypeProvider: CredentialsTypeProvider,

): AuthenticationWebFilter(authenticationManager) {

    init {
        setServerAuthenticationConverter(
            CredentialsAuthenticationConverter(
                objectMapper,
                credentialsTypeProvider.getCredentialsType()
            )
        )

        setAuthenticationSuccessHandler { webFilterExchange, authentication ->
            handleSuccess(webFilterExchange, authentication)
        }
    }

    private fun handleSuccess(
        exchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        return exchange.exchange.session.flatMap { session ->
            // Wrap the authentication object inside a SecurityContext
            val securityContext = SecurityContextImpl(authentication)

            // Store the SecurityContext in the session under the correct key
            session.attributes["SPRING_SECURITY_CONTEXT"] = securityContext
            exchange.exchange.response.statusCode = org.springframework.http.HttpStatus.OK
            Mono.empty()  // Ensure response is left open for session management.
        }
    }

}