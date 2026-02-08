package br.com.fiapx.fiapxuser.application.usecase.commands.update

import br.com.fiapx.fiapxuser.domain.enums.UserRole
import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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

@DisplayName("UpdateUserUseCase - Testes Unitários")
class UpdateUserUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: UpdateUserUseCase

    @BeforeEach
    fun setup() {
        useCase = UpdateUserUseCase(userRepository)
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    inner class SuccessScenarios {

        @Test
        fun `deve atualizar usuario com sucesso quando usuario existe`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command = createValidCommand(id = userId, name = "Maria Silva Atualizada")
            val existingUser = createUser(id = userId, name = "Maria Silva")
            val updatedUser = existingUser.copy(
                name = command.name,
                updatedBy = command.updatedBy
            )

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(any()) } returns updatedUser

            // Act
            val result = useCase.execute(command)

            // Assert
            assertNotNull(result)
            assertEquals(userId, result.id)
            assertEquals("Maria Silva Atualizada", result.name)
            assertEquals(command.updatedBy, result.updatedBy)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve preservar dados originais nao alterados`() {
            // Arrange
            val userId = UUID.randomUUID()
            val originalCreatedBy = UUID.randomUUID()
            val originalCreatedAt = LocalDateTime.now().minusDays(10)
            val command = createValidCommand(id = userId, name = "Novo Nome")
            val existingUser = createUser(
                id = userId,
                name = "Nome Antigo",
                email = "original@example.com",
                phone = "11999999999",
                role = UserRole.ADMIN,
                createdBy = originalCreatedBy,
                createdAt = originalCreatedAt
            )

            val userSlot = slot<User>()
            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(name = command.name)

            // Act
            useCase.execute(command)

            // Assert
            val capturedUser = userSlot.captured
            assertEquals(userId, capturedUser.id)
            assertEquals("Novo Nome", capturedUser.name)
            assertEquals("original@example.com", capturedUser.email)
            assertEquals("11999999999", capturedUser.phone)
            assertEquals(UserRole.ADMIN, capturedUser.role)
            assertEquals(originalCreatedBy, capturedUser.createdBy)
            assertEquals(originalCreatedAt, capturedUser.createdAt)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve atualizar updatedBy com o valor fornecido no command`() {
            // Arrange
            val userId = UUID.randomUUID()
            val updatedBy = UUID.randomUUID()
            val command = createValidCommand(id = userId, updatedBy = updatedBy)
            val existingUser = createUser(id = userId)
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(updatedBy = updatedBy)

            // Act
            useCase.execute(command)

            // Assert
            val capturedUser = userSlot.captured
            assertEquals(updatedBy, capturedUser.updatedBy)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve atualizar usuario mesmo quando updatedBy for nulo anteriormente`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command = createValidCommand(id = userId)
            val existingUser = createUser(id = userId, updatedBy = null, updatedAt = null)

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(any()) } returns existingUser.copy(
                name = command.name,
                updatedBy = command.updatedBy
            )

            // Act
            val result = useCase.execute(command)

            // Assert
            assertNotNull(result)
            assertEquals(command.updatedBy, result.updatedBy)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve permitir atualizar usuario multiplas vezes`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command1 = createValidCommand(id = userId, name = "Nome 1")
            val command2 = createValidCommand(id = userId, name = "Nome 2")
            val existingUser = createUser(id = userId, name = "Nome Original")

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(any()) } returnsMany listOf(
                existingUser.copy(name = "Nome 1"),
                existingUser.copy(name = "Nome 2")
            )

            // Act
            val result1 = useCase.execute(command1)
            every { userRepository.findById(userId) } returns result1
            val result2 = useCase.execute(command2)

            // Assert
            assertEquals("Nome 1", result1.name)
            assertEquals("Nome 2", result2.name)
            verify(exactly = 2) { userRepository.findById(userId) }
            verify(exactly = 2) { userRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("Cenários de Erro")
    inner class ErrorScenarios {

        @Test
        fun `deve lancar IllegalArgumentException quando usuario nao encontrado`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command = createValidCommand(id = userId)

            every { userRepository.findById(userId) } returns null

            // Act & Assert
            val exception = assertThrows(IllegalArgumentException::class.java) {
                useCase.execute(command)
            }

            assertTrue(exception.message!!.contains("UPDATE_User_USE_CASE"))
            assertTrue(exception.message!!.contains("não encontrada"))
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { userRepository.save(any()) }
        }

        @Test
        fun `deve lancar Exception quando DataAccessException ao salvar`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command = createValidCommand(id = userId)
            val existingUser = createUser(id = userId)
            val dataAccessException = mockk<DataAccessException>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(any()) } throws dataAccessException

            // Act & Assert
            val exception = assertThrows(Exception::class.java) {
                useCase.execute(command)
            }

            assertTrue(exception.message!!.contains("UPDATE_User_USE_CASE"))
            assertTrue(exception.message!!.contains("Erro ao atualizar entidade"))
            assertEquals(dataAccessException, exception.cause)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve lancar excecao quando erro generico no repositorio ao buscar`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command = createValidCommand(id = userId)
            val runtimeException = RuntimeException("Database connection error")

            every { userRepository.findById(userId) } throws runtimeException

            // Act & Assert
            assertThrows(RuntimeException::class.java) {
                useCase.execute(command)
            }

            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { userRepository.save(any()) }
        }

        @Test
        fun `deve lancar excecao quando usuario foi deletado`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command = createValidCommand(id = userId)

            every { userRepository.findById(userId) } returns null

            // Act & Assert
            val exception = assertThrows(IllegalArgumentException::class.java) {
                useCase.execute(command)
            }

            assertNotNull(exception.message)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { userRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("Validação de Atualização")
    inner class UpdateValidation {

        @Test
        fun `deve atualizar apenas o campo name mantendo outros inalterados`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command = createValidCommand(id = userId, name = "Nome Atualizado")
            val existingUser = createUser(
                id = userId,
                name = "Nome Original",
                email = "test@example.com",
                deleted = false
            )
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

            // Act
            useCase.execute(command)

            // Assert
            val capturedUser = userSlot.captured
            assertEquals("Nome Atualizado", capturedUser.name)
            assertEquals("test@example.com", capturedUser.email)
            assertFalse(capturedUser.deleted)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve manter flag deleted como false apos atualizacao`() {
            // Arrange
            val userId = UUID.randomUUID()
            val command = createValidCommand(id = userId)
            val existingUser = createUser(id = userId, deleted = false)
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

            // Act
            useCase.execute(command)

            // Assert
            assertFalse(userSlot.captured.deleted)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }
    }

    // Helpers
    private fun createValidCommand(
        id: UUID = UUID.randomUUID(),
        name: String = "Nome Teste",
        email: String = "teste@example.com",
        password: String = "senha123",
        birthDate: LocalDate = LocalDate.of(1990, 1, 1),
        phone: String = "11987654321",
        updatedBy: UUID = UUID.randomUUID()
    ) = UpdateUserCommand(
        id = id,
        name = name,
        email = email,
        password = password,
        birthDate = birthDate,
        phone = phone,
        updatedBy = updatedBy
    )

    private fun createUser(
        id: UUID = UUID.randomUUID(),
        name: String = "Nome Original",
        email: String = "original@example.com",
        password: String = "senha123",
        birthDate: LocalDate = LocalDate.of(1990, 1, 1),
        phone: String = "11999999999",
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

