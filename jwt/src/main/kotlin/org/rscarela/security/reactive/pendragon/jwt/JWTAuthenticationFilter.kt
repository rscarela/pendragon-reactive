package org.rscarela.security.reactive.pendragon.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import org.rscarela.security.reactive.pendragon.core.CredentialsAuthenticationConverter
import org.rscarela.security.reactive.pendragon.core.credentials.UserCredentials
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import reactor.core.publisher.Mono

class JWTAuthenticationFilter(
    url: String,
    private val authenticationManager: ReactiveAuthenticationManager,
    private val tokenProvider: JWTTokenProvider,
    private val objectMapper: ObjectMapper,
    private val credentialsType: Class<out UserCredentials>
) : AuthenticationWebFilter(authenticationManager) {

    init {
        // Set up the path matcher to trigger this filter only for specific routes
        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(url))
        // Set up the authentication converter to extract credentials from the request body
        setServerAuthenticationConverter(CredentialsAuthenticationConverter(objectMapper, credentialsType))
        // Handle successful authentication by adding the JWT token to the response
        setAuthenticationSuccessHandler { webFilterExchange, authentication ->
            handleSuccess(webFilterExchange, authentication)
        }
    }

    private fun handleSuccess(
        exchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        tokenProvider.addAuthentication(exchange.exchange, authentication.name)
        return Mono.empty()
    }
}