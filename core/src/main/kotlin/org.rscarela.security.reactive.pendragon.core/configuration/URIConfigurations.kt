package org.rscarela.security.reactive.pendragon.core.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component

/**
 * Class responsible for receiving, parsing, and mapping
 * URLs rules that will be enforced by the application,
 * adding permit and deny rules based on application properties.
 *
 * The following items can be configured:
 * * signUp - URI used for creating users
 * * signIn - URI used for authenticating
 * * permit - All URIs that must permit access with no authentication
 * * deny - All URIs that will be blocked
 *
 * @since 1.0.0
 * @author Renan Scarela
 */
@Component
class URIConfigurations(
    @Value("\${pendragon.filter.uri.signUp}") private val signUpURI: String,
    @Value("\${pendragon.filter.uri.signIn}") private val signInURI: String,
    @Value("\${pendragon.filter.uri.permit:[]}") permittedURIs: List<String>,
    @Value("\${pendragon.filter.uri.deny:[]}") deniedURIs: List<String>
) {

    private val mappedPermittedURIs: MutableMap<HttpMethod, Array<String>> = mutableMapOf()
    private val mappedDeniedURIs: MutableMap<HttpMethod, Array<String>> = mutableMapOf()

    init {
        val updatedPermittedURIs = permittedURIs.toMutableList().apply {
            add(signUpURI)
            add(signInURI)
        }

        mapPermittedURIs(updatedPermittedURIs)
        mapDeniedURIs(deniedURIs)
    }

    fun getPermittedURIs(): Map<HttpMethod, Array<String>> =
        mappedPermittedURIs.toMap()

    fun getDeniedURIs(): Map<HttpMethod, Array<String>> =
        mappedDeniedURIs.toMap()

    fun getSignInPath(): String = signInURI.split(" ")[1]

    private fun mapPermittedURIs(permittedURIs: List<String>) {
        for (method in HttpMethod.values()) {
            val mappedURIs = extract(method, permittedURIs)
            mappedPermittedURIs[method] = mappedURIs.toTypedArray()
        }
    }

    private fun mapDeniedURIs(deniedURIs: List<String>) {
        for (method in HttpMethod.values()) {
            val mappedURIs = extract(method, deniedURIs)
            mappedDeniedURIs[method] = mappedURIs.toTypedArray()
        }
    }

    private fun extract(method: HttpMethod, uris: List<String>): List<String> {
        if (uris.isEmpty()) return emptyList()

        return uris.filter { uri ->
            uri.contains(" ") && uri.split(" ")[0] == method.toString()
        }.map { uri -> uri.split(" ")[1] }
    }
}
