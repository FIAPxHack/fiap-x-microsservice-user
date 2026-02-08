package br.com.fiapx.fiapxuser.application.usecase.queries.getbyid

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

@DisplayName("GetUserByIdUseCase - Testes Unitários")
class GetUserByIdUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: GetUserByIdUseCase

    @BeforeEach
    fun setup() {
        useCase = GetUserByIdUseCase(userRepository)
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    inner class SuccessScenarios {

        @Test
        fun `deve retornar usuario quando encontrado por id`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(id = userId, name = "João Silva")

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(userId, result?.id)
            assertEquals("João Silva", result?.name)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve retornar null quando usuario nao encontrado`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)

            every { userRepository.findById(userId) } returns null

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNull(result)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve retornar usuario com todos os dados corretos`() {
            // Arrange
            val userId = UUID.randomUUID()
            val createdBy = UUID.randomUUID()
            val createdAt = LocalDateTime.now().minusDays(10)
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(
                id = userId,
                name = "Maria Santos",
                email = "maria@example.com",
                phone = "11987654321",
                role = UserRole.USER,
                createdBy = createdBy,
                createdAt = createdAt
            )

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(userId, result?.id)
            assertEquals("Maria Santos", result?.name)
            assertEquals("maria@example.com", result?.email)
            assertEquals("11987654321", result?.phone)
            assertEquals(UserRole.USER, result?.role)
            assertEquals(createdBy, result?.createdBy)
            assertEquals(createdAt, result?.createdAt)
            assertFalse(result?.deleted ?: true)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve buscar usuario por diferentes IDs`() {
            // Arrange
            val userId1 = UUID.randomUUID()
            val userId2 = UUID.randomUUID()
            val query1 = GetUserByIdQuery(id = userId1)
            val query2 = GetUserByIdQuery(id = userId2)
            val user1 = createUser(id = userId1, name = "Usuário 1")
            val user2 = createUser(id = userId2, name = "Usuário 2")

            every { userRepository.findById(userId1) } returns user1
            every { userRepository.findById(userId2) } returns user2

            // Act
            val result1 = useCase.execute(query1)
            val result2 = useCase.execute(query2)

            // Assert
            assertEquals("Usuário 1", result1?.name)
            assertEquals("Usuário 2", result2?.name)
            verify(exactly = 1) { userRepository.findById(userId1) }
            verify(exactly = 1) { userRepository.findById(userId2) }
        }

        @Test
        fun `deve retornar usuario mesmo se updatedBy for null`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(
                id = userId,
                updatedBy = null,
                updatedAt = null
            )

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertNull(result?.updatedBy)
            assertNull(result?.updatedAt)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve retornar usuario com role ADMIN`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(id = userId, role = UserRole.ADMIN)

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(UserRole.ADMIN, result?.role)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve retornar usuario com role SYSTEM`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(id = userId, role = UserRole.SYSTEM)

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(UserRole.SYSTEM, result?.role)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve retornar usuario com role USER`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(id = userId, role = UserRole.USER)

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(UserRole.USER, result?.role)
            verify(exactly = 1) { userRepository.findById(userId) }
        }
    }

    @Nested
    @DisplayName("Cenários de Erro")
    inner class ErrorScenarios {

        @Test
        fun `deve lancar Exception quando DataAccessException`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val dataAccessException = mockk<DataAccessException>()

            every { userRepository.findById(userId) } throws dataAccessException

            // Act & Assert
            val exception = assertThrows(Exception::class.java) {
                useCase.execute(query)
            }

            assertTrue(exception.message!!.contains("GET_BY_ID_USER_USE_CASE"))
            assertTrue(exception.message!!.contains("Falha ao tentar encontrar o usuário"))
            assertTrue(exception.message!!.contains(userId.toString()))
            assertEquals(dataAccessException, exception.cause)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve lancar excecao quando erro generico no repositorio`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val runtimeException = RuntimeException("Database connection error")

            every { userRepository.findById(userId) } throws runtimeException

            // Act & Assert
            assertThrows(RuntimeException::class.java) {
                useCase.execute(query)
            }

            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve incluir ID no erro quando DataAccessException`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val dataAccessException = mockk<DataAccessException>()

            every { userRepository.findById(userId) } throws dataAccessException

            // Act & Assert
            val exception = assertThrows(Exception::class.java) {
                useCase.execute(query)
            }

            assertTrue(exception.message!!.contains(userId.toString()))
            verify(exactly = 1) { userRepository.findById(userId) }
        }
    }

    @Nested
    @DisplayName("Validação de Busca")
    inner class SearchValidation {

        @Test
        fun `deve retornar null para ID inexistente sem lancar excecao`() {
            // Arrange
            val nonExistentId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = nonExistentId)

            every { userRepository.findById(nonExistentId) } returns null

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNull(result)
            verify(exactly = 1) { userRepository.findById(nonExistentId) }
        }

        @Test
        fun `deve buscar usuario que foi atualizado`() {
            // Arrange
            val userId = UUID.randomUUID()
            val updatedBy = UUID.randomUUID()
            val updatedAt = LocalDateTime.now()
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(
                id = userId,
                name = "Nome Atualizado",
                updatedBy = updatedBy,
                updatedAt = updatedAt
            )

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals("Nome Atualizado", result?.name)
            assertEquals(updatedBy, result?.updatedBy)
            assertEquals(updatedAt, result?.updatedAt)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve retornar usuario nao deletado`() {
            // Arrange
            val userId = UUID.randomUUID()
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(id = userId, deleted = false)

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertFalse(result?.deleted ?: true)
            verify(exactly = 1) { userRepository.findById(userId) }
        }

        @Test
        fun `deve buscar usuario por UUID valido`() {
            // Arrange
            val userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
            val query = GetUserByIdQuery(id = userId)
            val expectedUser = createUser(id = userId)

            every { userRepository.findById(userId) } returns expectedUser

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(userId, result?.id)
            verify(exactly = 1) { userRepository.findById(userId) }
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

