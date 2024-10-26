## Usage

### Import session dependency
```xml
<dependency>
    <groupId>org.rscarela.security.reactive.pendragon</groupId>
    <artifactId>jwt</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Import module configuration

```Kotlin
@SpringBootApplication
@Import(JWTConfiguration::class)
class SpringApplication {
```

### Add required properties

Secret is optional, but recommended. Using a secret would ensure the key won't change between multiple runs.

```yaml
pendragon:
  jwt:
    secret: my-secret
  filter:
    uri:
      signUp: POST /auth/signup
      signIn: POST /auth
```

### Create the auth controller

It should respect the paths defined on the properties.

This is only needed for sign up.

```Kotlin
@RestController
@RequestMapping("/auth")
class AuthenticationController {
    @PostMapping("/signup")
    fun register(): ResponseEntity<Any> = ResponseEntity.ok().build()
}
```