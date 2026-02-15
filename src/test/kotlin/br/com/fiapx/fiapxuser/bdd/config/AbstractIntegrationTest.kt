package br.com.fiapx.fiapxuser.bdd.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
abstract class AbstractIntegrationTest {

    companion object {
        @Container
        private val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(true)

        private fun ensureContainerStarted() {
            if (!postgresContainer.isRunning) {
                postgresContainer.start()
            }
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            ensureContainerStarted()

            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
//            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }

            registry.add("spring.jpa.hibernate.ddl-auto") { "none" }
            registry.add("spring.flyway.enabled") { "true" }
            registry.add("spring.flyway.clean-disabled") { "false" }
        }

        init {
            postgresContainer.start()
        }
    }
}
