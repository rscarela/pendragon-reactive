package org.rscarela.security.reactive.pendragon.jwt

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class JWTFilter(
    private val jwtTokenProvider: JWTTokenProvider
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
        Mono.justOrEmpty(jwtTokenProvider.getAuthentication(exchange)).flatMap {
            val securityContext = ReactiveSecurityContextHolder.withAuthentication(it)
            chain.filter(exchange).contextWrite(securityContext)
        }.switchIfEmpty {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            chain.filter(exchange)
        }
}