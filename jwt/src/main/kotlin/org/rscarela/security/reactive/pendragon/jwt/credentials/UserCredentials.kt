package org.rscarela.security.reactive.pendragon.jwt.credentials

interface UserCredentials {

    /**
     * @return username required for authentication.
     */
    fun getUsername(): String

    /**
     * @return password required for authentication.
     */
    fun getPassword(): String
}
