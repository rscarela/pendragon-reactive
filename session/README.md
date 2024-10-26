## Usage

### Import session dependency
```xml
<dependency>
    <groupId>org.rscarela.security.reactive.pendragon</groupId>
    <artifactId>session</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Add Redis Configuration

```Kotlin
@Configuration
@EnableRedisWebSession
class RedisConfiguration {

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory =
        LettuceConnectionFactory()

}
```

### Import module configuration

```Kotlin
@SpringBootApplication
@Import(SessionConfiguration::class)
class SpringApplication {
```

### Add required properties

```yaml
spring:
  redis:
    host: localhost
    port: 6379
  session:
    store-type: redis
    timeout: 30m  # Session timeout after 30 minutes
    cookie:
      name: SESSION  # Ensure the cookie name is SESSION
pendragon:
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