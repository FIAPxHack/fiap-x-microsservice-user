package br.com.fiapx.fiapxuser.application.usecase.queries.getbyemail

import br.com.fiapx.fiapxuser.domain.enums.UserRole
import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.DataAccessException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("GetUserByEmailUseCase - Testes Unitários")
class GetUserByEmailUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: GetUserByEmailUseCase

    @BeforeEach
    fun setup() {
        useCase = GetUserByEmailUseCase(userRepository)
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    inner class SuccessScenarios {

        @Test
        fun `deve retornar usuario quando encontrado por email`() {
            // Arrange
            val email = "joao@example.com"
            val query = GetUserByEmailQuery(email = email)
            val expectedUser = createUser(email = email, name = "João Silva")

            every { userRepository.findByEmail(email) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(email, result?.email)
            assertEquals("João Silva", result?.name)
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }

        @Test
        fun `deve retornar null quando usuario nao encontrado por email`() {
            // Arrange
            val email = "inexistente@example.com"
            val query = GetUserByEmailQuery(email = email)

            every { userRepository.findByEmail(email) } returns null

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNull(result)
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }

        @Test
        fun `deve retornar usuario com todos os dados corretos`() {
            // Arrange
            val email = "maria@example.com"
            val userId = UUID.randomUUID()
            val createdBy = UUID.randomUUID()
            val createdAt = LocalDateTime.now().minusDays(10)
            val query = GetUserByEmailQuery(email = email)
            val expectedUser = createUser(
                id = userId,
                name = "Maria Santos",
                email = email,
                phone = "11987654321",
                role = UserRole.USER,
                createdBy = createdBy,
                createdAt = createdAt
            )

            every { userRepository.findByEmail(email) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(userId, result?.id)
            assertEquals("Maria Santos", result?.name)
            assertEquals(email, result?.email)
            assertEquals("11987654321", result?.phone)
            assertEquals(UserRole.USER, result?.role)
            assertEquals(createdBy, result?.createdBy)
            assertEquals(createdAt, result?.createdAt)
            assertFalse(result?.deleted ?: true)
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }

        @Test
        fun `deve buscar usuario por diferentes emails`() {
            // Arrange
            val email1 = "user1@example.com"
            val email2 = "user2@example.com"
            val query1 = GetUserByEmailQuery(email = email1)
            val query2 = GetUserByEmailQuery(email = email2)
            val user1 = createUser(email = email1, name = "Usuário 1")
            val user2 = createUser(email = email2, name = "Usuário 2")

            every { userRepository.findByEmail(email1) } returns user1
            every { userRepository.findByEmail(email2) } returns user2

            // Act
            val result1 = useCase.execute(query1)
            val result2 = useCase.execute(query2)

            // Assert
            assertEquals("Usuário 1", result1?.name)
            assertEquals("Usuário 2", result2?.name)
            verify(exactly = 1) { userRepository.findByEmail(email1) }
            verify(exactly = 1) { userRepository.findByEmail(email2) }
        }

        @Test
        fun `deve retornar usuario com role ADMIN`() {
            // Arrange
            val email = "admin@example.com"
            val query = GetUserByEmailQuery(email = email)
            val expectedUser = createUser(email = email, role = UserRole.ADMIN)

            every { userRepository.findByEmail(email) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(UserRole.ADMIN, result?.role)
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }

        @Test
        fun `deve retornar usuario com role SYSTEM`() {
            // Arrange
            val email = "system@example.com"
            val query = GetUserByEmailQuery(email = email)
            val expectedUser = createUser(email = email, role = UserRole.SYSTEM)

            every { userRepository.findByEmail(email) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(UserRole.SYSTEM, result?.role)
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }

        @Test
        fun `deve retornar usuario com role USER`() {
            // Arrange
            val email = "user@example.com"
            val query = GetUserByEmailQuery(email = email)
            val expectedUser = createUser(email = email, role = UserRole.USER)

            every { userRepository.findByEmail(email) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(UserRole.USER, result?.role)
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }

        @Test
        fun `deve retornar usuario nao deletado`() {
            // Arrange
            val email = "ativo@example.com"
            val query = GetUserByEmailQuery(email = email)
            val expectedUser = createUser(email = email, deleted = false)

            every { userRepository.findByEmail(email) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertFalse(result?.deleted ?: true)
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }
    }

    @Nested
    @DisplayName("Cenários de Erro")
    inner class ErrorScenarios {

        @Test
        fun `deve lancar Exception quando DataAccessException`() {
            // Arrange
            val email = "erro@example.com"
            val query = GetUserByEmailQuery(email = email)
            val dataAccessException = mockk<DataAccessException>()
            every { dataAccessException.message } returns "Database error"

            every { userRepository.findByEmail(email) } throws dataAccessException

            // Act & Assert
            val exception = assertThrows(Exception::class.java) {
                useCase.execute(query)
            }

            assertTrue(exception.message!!.contains("GET_BY_EMAIL_USER_USE_CASE"))
            assertTrue(exception.message!!.contains("Falha ao tentar encontrar o usuário"))
            assertTrue(exception.message!!.contains(email))
            assertEquals(dataAccessException, exception.cause)
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }

        @Test
        fun `deve lancar excecao quando erro generico no repositorio`() {
            // Arrange
            val email = "erro@example.com"
            val query = GetUserByEmailQuery(email = email)
            val runtimeException = RuntimeException("Database connection error")

            every { userRepository.findByEmail(email) } throws runtimeException

            // Act & Assert
            assertThrows(RuntimeException::class.java) {
                useCase.execute(query)
            }

            verify(exactly = 1) { userRepository.findByEmail(email) }
        }

        @Test
        fun `deve incluir email no erro quando DataAccessException`() {
            // Arrange
            val email = "falha@example.com"
            val query = GetUserByEmailQuery(email = email)
            val dataAccessException = mockk<DataAccessException>()
            every { dataAccessException.message } returns "Database error"

            every { userRepository.findByEmail(email) } throws dataAccessException

            // Act & Assert
            val exception = assertThrows(Exception::class.java) {
                useCase.execute(query)
            }

            assertTrue(exception.message!!.contains(email))
            verify(exactly = 1) { userRepository.findByEmail(email) }
        }
    }

    // Helpers
    private fun createUser(
        id: UUID = UUID.randomUUID(),
        name: String = "Usuário Teste",
        email: String = "teste@example.com",
        password: String = "senha123",
        birthDate: LocalDate = LocalDate.of(1990, 1, 1),
        phone: String = "11987654321",
        role: UserRole = UserRole.ADMIN,
        createdBy: UUID = UUID.randomUUID(),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedBy: UUID? = null,
        updatedAt: LocalDateTime? = null,
        deleted: Boolean = false
    ) = User(
        id = id,
        name = name,
        email = email,
        password = password,
        birthDate = birthDate,
        phone = phone,
        role = role,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedBy = updatedBy,
        updatedAt = updatedAt,
        deleted = deleted
    )
}
