package org.rscarela.security.reactive.pendragon.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import org.rscarela.security.reactive.pendragon.jwt.credentials.UserCredentials
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

class JWTAuthenticationConverter(
    private val objectMapper: ObjectMapper,
    private val credentialsType: Class<out UserCredentials>,
) : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication?>? {
        return DataBufferUtils.join(exchange.request.body)
            .map { dataBuffer: DataBuffer ->
                val body = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer()).toString()
                DataBufferUtils.release(dataBuffer)  // Release the buffer to avoid memory leaks
                objectMapper.readValue(body, credentialsType)
            }
            .map { credentials ->
                UsernamePasswordAuthenticationToken(
                    credentials.getUsername(),
                    credentials.getPassword(),
                    emptyList()
                )
            }
    }
}