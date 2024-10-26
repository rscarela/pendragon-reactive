## Usage

Before following module specific instructions, make sure to follow the steps below

### Implement your user and credentials entities

You must have appropriate implementations for `AuthenticatedUser` and `UserCredentials`. Some modules might also require them to implement `Serializable`. 

Those are the models that your application will use internally as users (e.g. the user persisted in your database).
```Kotlin
class User(
    @Embedded
    var credentials: Credentials,
) : AuthenticatedUser, Serializable
```

```Kotlin
class Credentials(
    var email: String,
    private var password: String,
) : UserCredentials, Serializable
```

### Implement a CredentialsTypeProvider

This is a simple implementation that tells Pendragon to which type your credentials must be parsed to.

```Kotlin
@Component
class CredentialsTypeDefinition : CredentialsTypeProvider {
    override fun getCredentialsType(): Class<out UserCredentials> = Credentials::class.java
}
```

### Implement an AuthenticatedUserProvider

This is the class that knows how to load your User for authentication (e.g. how to load it from a database using name and password).

```Kotlin
@Component
class UserProvider(
    private val userRepository: UserRepository,
) : AuthenticatedUserProvider<User> {
    override fun findByUuid(uuid: String): Optional<User> = Optional.ofNullable(userRepository.findByUuid(uuid))

    override fun findByCredentials(
        username: String,
        password: String,
    ): Optional<User> =
        Optional.ofNullable(userRepository.findByCredentials_EmailAndCredentials_PasswordAndCredentials_Enabled(username, password))
}
```

## Modules

For specific modules configuration, refer to each README file.