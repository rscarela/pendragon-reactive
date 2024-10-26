package org.rscarela.security.reactive.pendragon.jwt.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.rscarela.security.reactive.pendragon.jwt.JWTAuthenticationFilter
import org.rscarela.security.reactive.pendragon.jwt.JWTAuthenticationManager
import org.rscarela.security.reactive.pendragon.jwt.JWTFilter
import org.rscarela.security.reactive.pendragon.jwt.JWTTokenProvider
import org.rscarela.security.reactive.pendragon.jwt.credentials.CredentialsTypeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher

@Configuration
@ComponentScan(basePackages = ["org.rscarela.security.reactive.pendragon.jwt"])
open class JWTConfiguration(
    private val jwtFilter: JWTFilter,
    private val uriConfigurations: URIConfigurations,
    private val tokenProvider: JWTTokenProvider,
    private val objectMapper: ObjectMapper,
    private val credentialsTypeProvider: CredentialsTypeProvider,
) {

    @Bean
    open fun securityWebFilterChain(httpSecurity: ServerHttpSecurity, reactiveAuthenticationManager: ReactiveAuthenticationManager): SecurityWebFilterChain {
        httpSecurity
            .csrf { it.disable() }

        return httpSecurity
            .csrf { it.disable() }
            .authorizeExchange { exchanges ->
                uriConfigurations.getPermittedURIs().forEach { (method, uris) ->
                    uris.forEach { uri ->
                        exchanges.matchers(
                            PathPatternParserServerWebExchangeMatcher(uri, method)
                        ).permitAll()
                    }
                }

                uriConfigurations.getDeniedURIs().forEach { (method, uris) ->
                    uris.forEach { uri ->
                        exchanges.matchers(
                            PathPatternParserServerWebExchangeMatcher(uri, method)
                        ).denyAll()
                    }
                }
                exchanges
                    .anyExchange()
                    .authenticated()
            }
            .addFilterAt(JWTAuthenticationFilter(uriConfigurations.getSignInPath(), reactiveAuthenticationManager, tokenProvider, objectMapper, credentialsTypeProvider.getCredentialsType()), SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

}