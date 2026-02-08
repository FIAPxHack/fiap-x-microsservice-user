package br.com.fiapx.fiapxuser.application.usecase.commands.delete

import br.com.fiapx.fiapxuser.domain.enums.UserRole
import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import io.mockk.every
import io.mockk.justRun
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

@DisplayName("DeleteUserUseCase - Testes Unitários")
class DeleteUserUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: DeleteUserUseCase

    @BeforeEach
    fun setup() {
        useCase = DeleteUserUseCase(userRepository)
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    inner class SuccessScenarios {

        @Test
        fun `deve deletar usuario com sucesso quando usuario existe`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val existingUser = createUser(id = userId, deleted = false)
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(deleted = true)

            // Act
            useCase.execute(command)

            // Assert
            val capturedUser = userSlot.captured
            assertTrue(capturedUser.deleted)
            assertEquals(deletedBy, capturedUser.updatedBy)
            assertEquals(userId, capturedUser.id)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve marcar deleted como true sem alterar outros campos`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val originalName = "João Silva"
            val originalEmail = "joao@example.com"
            val originalCreatedBy = UUID.randomUUID()
            val existingUser = createUser(
                id = userId,
                name = originalName,
                email = originalEmail,
                createdBy = originalCreatedBy,
                deleted = false
            )
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(deleted = true)

            // Act
            useCase.execute(command)

            // Assert
            val capturedUser = userSlot.captured
            assertTrue(capturedUser.deleted)
            assertEquals(userId, capturedUser.id)
            assertEquals(originalName, capturedUser.name)
            assertEquals(originalEmail, capturedUser.email)
            assertEquals(originalCreatedBy, capturedUser.createdBy)
            assertEquals(deletedBy, capturedUser.updatedBy)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve atualizar updatedBy com deletedBy ao deletar`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val existingUser = createUser(id = userId, updatedBy = null)
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(
                deleted = true,
                updatedBy = deletedBy
            )

            // Act
            useCase.execute(command)

            // Assert
            assertEquals(deletedBy, userSlot.captured.updatedBy)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve realizar soft delete mantendo dados do usuario`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val existingUser = createUser(
                id = userId,
                name = "Maria Santos",
                email = "maria@example.com",
                phone = "11987654321",
                role = UserRole.USER
            )
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(deleted = true)

            // Act
            useCase.execute(command)

            // Assert
            val capturedUser = userSlot.captured
            assertTrue(capturedUser.deleted)
            assertEquals("Maria Santos", capturedUser.name)
            assertEquals("maria@example.com", capturedUser.email)
            assertEquals("11987654321", capturedUser.phone)
            assertEquals(UserRole.USER, capturedUser.role)
            assertNotNull(capturedUser.id)
            assertNotNull(capturedUser.createdBy)
            assertNotNull(capturedUser.createdAt)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("Cenários de Erro")
    inner class ErrorScenarios {

        @Test
        fun `deve lancar IllegalArgumentException quando usuario nao encontrado`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)

            every { userRepository.findById(userId) } returns null

            // Act & Assert
            val exception = assertThrows(IllegalArgumentException::class.java) {
                useCase.execute(command)
            }

            assertTrue(exception.message!!.contains("DELETE_USER_USE_CASE"))
            assertTrue(exception.message!!.contains("não encontrada"))
            assertTrue(exception.message!!.contains(userId.toString()))
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { userRepository.save(any()) }
        }

        @Test
        fun `deve lancar Exception quando DataAccessException ao salvar`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val existingUser = createUser(id = userId)
            val dataAccessException = mockk<DataAccessException>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(any()) } throws dataAccessException

            // Act & Assert
            val exception = assertThrows(Exception::class.java) {
                useCase.execute(command)
            }

            assertTrue(exception.message!!.contains("DELETE_USER_USE_CASE"))
            assertTrue(exception.message!!.contains("Falha ao tentar deletar o usuário"))
            assertEquals(dataAccessException, exception.cause)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve lancar excecao quando erro generico no repositorio ao buscar`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
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
        fun `deve lancar excecao com ID correto quando usuario nao existe`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)

            every { userRepository.findById(userId) } returns null

            // Act & Assert
            val exception = assertThrows(IllegalArgumentException::class.java) {
                useCase.execute(command)
            }

            assertTrue(exception.message!!.contains(userId.toString()))
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 0) { userRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("Validação de Soft Delete")
    inner class SoftDeleteValidation {

        @Test
        fun `deve realizar soft delete sem remover fisicamente do banco`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val existingUser = createUser(id = userId)

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(any()) } returns existingUser.copy(deleted = true)

            // Act
            useCase.execute(command)

            // Assert
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve permitir deletar usuario que ja foi atualizado anteriormente`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val previousUpdatedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val existingUser = createUser(
                id = userId,
                updatedBy = previousUpdatedBy,
                updatedAt = LocalDateTime.now().minusDays(5)
            )
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(deleted = true)

            // Act
            useCase.execute(command)

            // Assert
            val capturedUser = userSlot.captured
            assertTrue(capturedUser.deleted)
            assertEquals(deletedBy, capturedUser.updatedBy)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `nao deve alterar createdBy ao deletar usuario`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val originalCreatedBy = UUID.randomUUID()
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val existingUser = createUser(id = userId, createdBy = originalCreatedBy)
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(deleted = true)

            // Act
            useCase.execute(command)

            // Assert
            assertEquals(originalCreatedBy, userSlot.captured.createdBy)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `nao deve alterar createdAt ao deletar usuario`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()
            val originalCreatedAt = LocalDateTime.now().minusDays(30)
            val command = DeleteUserCommand(id = userId, deletedBy = deletedBy)
            val existingUser = createUser(id = userId, createdAt = originalCreatedAt)
            val userSlot = slot<User>()

            every { userRepository.findById(userId) } returns existingUser
            every { userRepository.save(capture(userSlot)) } returns existingUser.copy(deleted = true)

            // Act
            useCase.execute(command)

            // Assert
            assertEquals(originalCreatedAt, userSlot.captured.createdAt)
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userRepository.save(any()) }
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

