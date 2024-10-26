package org.rscarela.security.reactive.pendragon.jwt.credentials

/**
 * Representation of any class that must behave as a
 * user that can be authenticated.
 *
 * An AuthenticatedUser can carry any behaviors and
 * attributes, as long as it implements a getter for
 * a unique identifier.
 *
 * @see AuthenticatedUserProvider
 *
 * @since 1.0.0
 * @author Renan Scarela
 */
interface AuthenticatedUser {

    /**
     * A unique identifier is the only requirement for
     * defining a user that can be authenticated.
     *
     * The UUID can be any attribute that makes sense
     * to the application to handle as unique (e.g.
     * generated unique ids, username, email, etc).
     *
     * Please, mind that the uuid is supposed to be used
     * by the AuthenticatedUserProvider to load the actual
     * class identified by this id.
     *
     * @return authenticated user unique identifier
     */
    fun getUuid(): String

}