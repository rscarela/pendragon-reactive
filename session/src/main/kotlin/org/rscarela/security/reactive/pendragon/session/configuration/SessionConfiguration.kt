package org.rscarela.security.reactive.pendragon.session.configuration

import org.rscarela.security.reactive.pendragon.core.configuration.URIConfigurations
import org.rscarela.security.reactive.pendragon.session.SessionWebFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher

@Configuration
@EnableWebFluxSecurity
@ComponentScan(basePackages = ["org.rscarela.security.reactive.pendragon"])
open class SessionConfiguration(
    private val uriConfigurations: URIConfigurations,
    private val sessionWebFilter: SessionWebFilter,
) {

    @Bean
    open fun securityWebFilterChain(httpSecurity: ServerHttpSecurity, reactiveAuthenticationManager: ReactiveAuthenticationManager): SecurityWebFilterChain {
        httpSecurity
            .csrf { it.disable() }

        httpSecurity.authenticationManager(reactiveAuthenticationManager)

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
            .addFilterAt(sessionWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .logout {
                it.logoutHandler(logoutHandler())
            }
            .build()
    }

    @Bean
    open fun logoutHandler(): ServerLogoutHandler = WebSessionServerLogoutHandler()

}