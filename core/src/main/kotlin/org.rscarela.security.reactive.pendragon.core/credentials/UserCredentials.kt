package org.rscarela.security.reactive.pendragon.core.credentials

import java.io.Serializable

interface UserCredentials : Serializable {

    /**
     * @return username required for authentication.
     */
    fun getUsername(): String

    /**
     * @return password required for authentication.
     */
    fun getPassword(): String
}
