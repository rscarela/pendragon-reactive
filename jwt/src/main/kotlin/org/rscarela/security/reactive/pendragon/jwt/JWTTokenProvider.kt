package org.rscarela.security.reactive.pendragon.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import org.rscarela.security.reactive.pendragon.jwt.credentials.AuthenticatedUser
import org.rscarela.security.reactive.pendragon.jwt.credentials.AuthenticatedUserProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.Date

import javax.crypto.SecretKey

@Component
class JWTTokenProvider @Autowired constructor(
    private val authenticatedUserProvider: AuthenticatedUserProvider<AuthenticatedUser>,
    @Value("\${pendragon.jwt.secret:null}") private val secret: String?,
    @Value("\${pendragon.jwt.expiration:860000000}") private val expirationTime: Long,
    @Value("\${pendragon.jwt.header.prefix:Bearer}") private val headerPrefix: String,
    @Value("\${pendragon.jwt.header.name:Authorization}") private val headerName: String,
    private val key: SecretKey = if (secret == null) Keys.secretKeyFor(SignatureAlgorithm.HS512) else Keys.hmacShaKeyFor(secret.toByteArray()),
) {

    /**
     * Invoked during authentication, generates the JWT token and adds it
     * to the response header.
     *
     * @param response - current HttpServletResponse
     * @param username - Username that identifies the user that is authenticating
     */
    fun addAuthentication(exchange: ServerWebExchange, username: String) {
        val jwt = Jwts.builder()
            .setSubject(username)
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(key)
            .compact()

        exchange.response.headers.add(headerName, "$headerPrefix $jwt")
    }

    /**
     * Loads an Authentication instance for the provided JWT token.
     *
     * The token is parsed to retrieve its unique identifier. Then,
     * the AuthenticatedUserProvider is invoked to retrieve the
     * authenticated user and create the authentication.
     *
     * @param request - current HttpServletRequest
     * @return UserAuthentication parsed from the current JWT token
     */
    fun getAuthentication(exchange: ServerWebExchange): Authentication? =
        exchange.request.headers[headerName]?.firstOrNull()?.let { token ->
            val uuid = getParsedUuid(token) ?: return null
            val user = authenticatedUserProvider.findByUuid(uuid)
            return if (user.isEmpty) null else UserAuthentication(user.get())
        }

    /**
     * Parses the Authorization header to retrieve the JWT token content.
     *
     * If expired or malformed, it returns null and logs the error.
     *
     * @param token - JWT token from the Authorization header
     * @return Parsed UUID or null if invalid
     */
    private fun getParsedUuid(token: String): String? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace(headerPrefix, "").trim())
                .body
                .subject
        } catch (e: ExpiredJwtException) {
            println("Expired token")
            null
        } catch (e: UnsupportedJwtException) {
            println("Invalid token: unsupported")
            null
        } catch (e: MalformedJwtException) {
            println("Invalid token: malformed")
            null
        }
    }
}
