package br.com.fiapx.fiapxuser.application.usecase.commands.create

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

@DisplayName("CreateUserUseCase - Testes Unitários")
class CreateUserUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: CreateUserUseCase

    @BeforeEach
    fun setup() {
        useCase = CreateUserUseCase(userRepository)
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    inner class SuccessScenarios {

        @Test
        fun `deve criar usuario com sucesso quando dados validos`() {
            // Arrange
            val command = createValidCommand()
            val userSlot = slot<User>()

            every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

            // Act
            val result = useCase.execute(command)

            // Assert
            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(command.name, result.name)
            assertEquals(command.email, result.email)
            assertEquals(command.password, result.password)
            assertEquals(command.birthDate, result.birthDate)
            assertEquals(command.phone, result.phone)
            assertEquals(UserRole.ADMIN, result.role)
            assertEquals(command.createdBy, result.createdBy)
            assertNotNull(result.createdAt)
            assertFalse(result.deleted)

            verify(exactly = 1) { userRepository.save(any()) }

            // Verifica que o usuário capturado tem os dados corretos
            val capturedUser = userSlot.captured
            assertEquals(command.name, capturedUser.name)
            assertEquals(command.email, capturedUser.email)
            assertEquals(UserRole.ADMIN, capturedUser.role)
        }

        @Test
        fun `deve criar usuario com role SYSTEM quando role code 0`() {
            // Arrange
            val command = createValidCommand(role = 0)
            val savedUser = createUser(role = UserRole.SYSTEM)

            every { userRepository.save(any()) } returns savedUser

            // Act
            val result = useCase.execute(command)

            // Assert
            assertEquals(UserRole.SYSTEM, result.role)
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve criar usuario com role USER quando role code 2`() {
            // Arrange
            val command = createValidCommand(role = 2)
            val savedUser = createUser(role = UserRole.USER)

            every { userRepository.save(any()) } returns savedUser

            // Act
            val result = useCase.execute(command)

            // Assert
            assertEquals(UserRole.USER, result.role)
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve gerar UUID automaticamente para novo usuario`() {
            // Arrange
            val command = createValidCommand()
            val userSlot = slot<User>()
            val savedUser = createUser()

            every { userRepository.save(capture(userSlot)) } returns savedUser

            // Act
            useCase.execute(command)

            // Assert
            val capturedUser = userSlot.captured
            assertNotNull(capturedUser.id)
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve definir createdAt automaticamente`() {
            // Arrange
            val command = createValidCommand()
            val userSlot = slot<User>()
            val savedUser = createUser()

            every { userRepository.save(capture(userSlot)) } returns savedUser

            // Act
            val before = LocalDateTime.now()
            useCase.execute(command)
            val after = LocalDateTime.now()

            // Assert
            val capturedUser = userSlot.captured
            assertNotNull(capturedUser.createdAt)
            assertTrue(capturedUser.createdAt.isAfter(before) || capturedUser.createdAt.isEqual(before))
            assertTrue(capturedUser.createdAt.isBefore(after) || capturedUser.createdAt.isEqual(after))
            verify(exactly = 1) { userRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("Cenários de Erro")
    inner class ErrorScenarios {

        @Test
        fun `deve lancar excecao quando role code invalido`() {
            // Arrange
            val command = createValidCommand(role = 999)

            // Act & Assert
            val exception = assertThrows(IllegalArgumentException::class.java) {
                useCase.execute(command)
            }

            assertTrue(exception.message!!.contains("não foi encontrado"))
            verify(exactly = 0) { userRepository.save(any()) }
        }

        @Test
        fun `deve lancar excecao quando role code negativo`() {
            // Arrange
            val command = createValidCommand(role = -1)

            // Act & Assert
            val exception = assertThrows(IllegalArgumentException::class.java) {
                useCase.execute(command)
            }

            assertTrue(exception.message!!.contains("não foi encontrado"))
            verify(exactly = 0) { userRepository.save(any()) }
        }

        @Test
        fun `deve lancar Exception quando erro de DataAccessException`() {
            // Arrange
            val command = createValidCommand()
            val dataAccessException = mockk<DataAccessException>()

            every { userRepository.save(any()) } throws dataAccessException

            // Act & Assert
            val exception = assertThrows(Exception::class.java) {
                useCase.execute(command)
            }

            assertTrue(exception.message!!.contains("CREATE_USER_USE_CASE"))
            assertTrue(exception.message!!.contains("Falha ao tentar criar o usuário"))
            assertEquals(dataAccessException, exception.cause)
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve propagar excecao quando erro generico no repositorio`() {
            // Arrange
            val command = createValidCommand()
            val runtimeException = RuntimeException("Database error")

            every { userRepository.save(any()) } throws runtimeException

            // Act & Assert
            assertThrows(RuntimeException::class.java) {
                useCase.execute(command)
            }

            verify(exactly = 1) { userRepository.save(any()) }
        }
    }

    @Nested
    @DisplayName("Validação de Campos")
    inner class FieldValidation {

        @Test
        fun `deve criar usuario com email em formato correto`() {
            // Arrange
            val command = createValidCommand(email = "user@example.com")
            val savedUser = createUser(email = "user@example.com")

            every { userRepository.save(any()) } returns savedUser

            // Act
            val result = useCase.execute(command)

            // Assert
            assertEquals("user@example.com", result.email)
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve criar usuario com telefone no formato correto`() {
            // Arrange
            val command = createValidCommand(phone = "11987654321")
            val savedUser = createUser(phone = "11987654321")

            every { userRepository.save(any()) } returns savedUser

            // Act
            val result = useCase.execute(command)

            // Assert
            assertEquals("11987654321", result.phone)
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `deve criar usuario com data de nascimento no passado`() {
            // Arrange
            val birthDate = LocalDate.of(1990, 1, 1)
            val command = createValidCommand(birthDate = birthDate)
            val savedUser = createUser(birthDate = birthDate)

            every { userRepository.save(any()) } returns savedUser

            // Act
            val result = useCase.execute(command)

            // Assert
            assertEquals(birthDate, result.birthDate)
            verify(exactly = 1) { userRepository.save(any()) }
        }
    }

    // Helpers
    private fun createValidCommand(
        name: String = "João Silva",
        email: String = "joao@example.com",
        password: String = "senha123",
        birthDate: LocalDate = LocalDate.of(1990, 1, 1),
        phone: String = "11987654321",
        role: Int = 1,
        createdBy: UUID = UUID.randomUUID()
    ) = CreateUserCommand(
        name = name,
        email = email,
        password = password,
        birthDate = birthDate,
        phone = phone,
        role = role,
        createdBy = createdBy
    )

    private fun createUser(
        id: UUID = UUID.randomUUID(),
        name: String = "João Silva",
        email: String = "joao@example.com",
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

