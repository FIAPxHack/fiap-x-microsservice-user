package br.com.fiapx.fiapxuser.adapter.mapper

import br.com.fiapx.fiapxuser.adapter.request.commands.create.CreateUserRequest
import br.com.fiapx.fiapxuser.adapter.request.commands.update.UpdateUserRequest
import br.com.fiapx.fiapxuser.domain.common.Paged
import br.com.fiapx.fiapxuser.domain.enums.UserRole
import br.com.fiapx.fiapxuser.domain.model.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("UserMapper - Testes Unitários")
class UserMapperTest {

    @Nested
    @DisplayName("toCreateCommand")
    inner class ToCreateCommand {

        @Test
        fun `deve converter CreateUserRequest para CreateUserCommand corretamente`() {
            // Arrange
            val request = CreateUserRequest(
                name = "João Silva",
                email = "joao@example.com",
                password = "senha123",
                birthDate = LocalDate.of(1990, 1, 1),
                phone = "11987654321",
                role = 1
            )
            val createdBy = UUID.randomUUID()

            // Act
            val command = UserMapper.toCreateCommand(request, createdBy)

            // Assert
            assertEquals(request.name, command.name)
            assertEquals(request.email, command.email)
            assertEquals(request.password, command.password)
            assertEquals(request.birthDate, command.birthDate)
            assertEquals(request.phone, command.phone)
            assertEquals(request.role, command.role)
            assertEquals(createdBy, command.createdBy)
        }

        @Test
        fun `deve mapear todos os roles corretamente`() {
            // Arrange
            val request = CreateUserRequest(
                name = "Test",
                email = "test@example.com",
                password = "pass",
                birthDate = LocalDate.of(1990, 1, 1),
                phone = "11999999999",
                role = 2
            )
            val createdBy = UUID.randomUUID()

            // Act
            val command = UserMapper.toCreateCommand(request, createdBy)

            // Assert
            assertEquals(2, command.role)
        }

        @Test
        fun `deve preservar UUID de createdBy fornecido`() {
            // Arrange
            val specificUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
            val request = CreateUserRequest(
                name = "Test",
                email = "test@example.com",
                password = "pass",
                birthDate = LocalDate.of(1990, 1, 1),
                phone = "11999999999",
                role = 1
            )

            // Act
            val command = UserMapper.toCreateCommand(request, specificUuid)

            // Assert
            assertEquals(specificUuid, command.createdBy)
        }
    }

    @Nested
    @DisplayName("toUpdateCommand")
    inner class ToUpdateCommand {

        @Test
        fun `deve converter UpdateUserRequest para UpdateUserCommand corretamente`() {
            // Arrange
            val userId = UUID.randomUUID()
            val request = UpdateUserRequest(
                id = userId,
                name = "Maria Santos",
                email = "maria@example.com",
                password = "senha456",
                birthDate = LocalDate.of(1985, 5, 15),
                phone = "11976543210"
            )
            val updatedBy = UUID.randomUUID()

            // Act
            val command = UserMapper.toUpdateCommand(request, updatedBy)

            // Assert
            assertEquals(request.id, command.id)
            assertEquals(request.name, command.name)
            assertEquals(request.email, command.email)
            assertEquals(request.password, command.password)
            assertEquals(request.birthDate, command.birthDate)
            assertEquals(request.phone, command.phone)
            assertEquals(updatedBy, command.updatedBy)
        }

        @Test
        fun `deve preservar ID do usuario no command`() {
            // Arrange
            val specificUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
            val request = UpdateUserRequest(
                id = specificUserId,
                name = "Test",
                email = "test@example.com",
                password = "pass",
                birthDate = LocalDate.of(1990, 1, 1),
                phone = "11999999999"
            )
            val updatedBy = UUID.randomUUID()

            // Act
            val command = UserMapper.toUpdateCommand(request, updatedBy)

            // Assert
            assertEquals(specificUserId, command.id)
        }

        @Test
        fun `deve preservar UUID de updatedBy fornecido`() {
            // Arrange
            val specificUuid = UUID.fromString("987e6543-e21b-34d5-a678-123456789012")
            val request = UpdateUserRequest(
                id = UUID.randomUUID(),
                name = "Test",
                email = "test@example.com",
                password = "pass",
                birthDate = LocalDate.of(1990, 1, 1),
                phone = "11999999999"
            )

            // Act
            val command = UserMapper.toUpdateCommand(request, specificUuid)

            // Assert
            assertEquals(specificUuid, command.updatedBy)
        }
    }

    @Nested
    @DisplayName("toDeleteCommand")
    inner class ToDeleteCommand {

        @Test
        fun `deve converter ID e deletedBy para DeleteUserCommand corretamente`() {
            // Arrange
            val userId = UUID.randomUUID()
            val deletedBy = UUID.randomUUID()

            // Act
            val command = UserMapper.toDeleteCommand(userId, deletedBy)

            // Assert
            assertEquals(userId, command.id)
            assertEquals(deletedBy, command.deletedBy)
        }

        @Test
        fun `deve criar command com IDs especificos`() {
            // Arrange
            val specificUserId = UUID.fromString("111e1111-e11b-11d1-a111-111111111111")
            val specificDeletedBy = UUID.fromString("222e2222-e22b-22d2-a222-222222222222")

            // Act
            val command = UserMapper.toDeleteCommand(specificUserId, specificDeletedBy)

            // Assert
            assertEquals(specificUserId, command.id)
            assertEquals(specificDeletedBy, command.deletedBy)
        }
    }

    @Nested
    @DisplayName("toResponse")
    inner class ToResponse {

        @Test
        fun `deve converter User para UserResponse corretamente`() {
            // Arrange
            val userId = UUID.randomUUID()
            val createdAt = LocalDateTime.now()
            val updatedAt = LocalDateTime.now().plusDays(1)
            val user = createUser(
                id = userId,
                name = "Pedro Oliveira",
                email = "pedro@example.com",
                phone = "11965432109",
                role = UserRole.ADMIN,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            // Act
            val response = UserMapper.toResponse(user)

            // Assert
            assertEquals(userId, response.id)
            assertEquals("Pedro Oliveira", response.name)
            assertEquals("pedro@example.com", response.email)
            assertEquals("11965432109", response.phone)
            assertEquals(1, response.role) // ADMIN.code = 1
            assertEquals(createdAt, response.createdAt)
            assertEquals(updatedAt, response.updatedAt)
        }

        @Test
        fun `deve mapear role SYSTEM corretamente`() {
            // Arrange
            val user = createUser(role = UserRole.SYSTEM)

            // Act
            val response = UserMapper.toResponse(user)

            // Assert
            assertEquals(0, response.role) // SYSTEM.code = 0
        }

        @Test
        fun `deve mapear role USER corretamente`() {
            // Arrange
            val user = createUser(role = UserRole.USER)

            // Act
            val response = UserMapper.toResponse(user)

            // Assert
            assertEquals(2, response.role) // USER.code = 2
        }

        @Test
        fun `deve mapear role ADMIN corretamente`() {
            // Arrange
            val user = createUser(role = UserRole.ADMIN)

            // Act
            val response = UserMapper.toResponse(user)

            // Assert
            assertEquals(1, response.role) // ADMIN.code = 1
        }

        @Test
        fun `deve permitir updatedAt null`() {
            // Arrange
            val user = createUser(updatedAt = null)

            // Act
            val response = UserMapper.toResponse(user)

            // Assert
            assertNull(response.updatedAt)
        }

        @Test
        fun `deve converter usuario sem atualizacao`() {
            // Arrange
            val user = createUser(
                name = "Novo Usuario",
                updatedAt = null
            )

            // Act
            val response = UserMapper.toResponse(user)

            // Assert
            assertEquals("Novo Usuario", response.name)
            assertNull(response.updatedAt)
            assertNotNull(response.createdAt)
        }
    }

    @Nested
    @DisplayName("toResponsePaged")
    inner class ToResponsePaged {

        @Test
        fun `deve converter Paged de User para Paged de UserResponse corretamente`() {
            // Arrange
            val users = listOf(
                createUser(name = "User 1", role = UserRole.ADMIN),
                createUser(name = "User 2", role = UserRole.USER),
                createUser(name = "User 3", role = UserRole.SYSTEM)
            )
            val pagedUsers = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 3,
                totalPages = 1
            )

            // Act
            val result = UserMapper.toResponsePaged(pagedUsers)

            // Assert
            assertEquals(3, result.items.size)
            assertEquals(0, result.page)
            assertEquals(10, result.pageSize)
            assertEquals(3, result.totalItems)
            assertEquals(1, result.totalPages)
            assertEquals("User 1", result.items[0].name)
            assertEquals("User 2", result.items[1].name)
            assertEquals("User 3", result.items[2].name)
            assertEquals(1, result.items[0].role) // ADMIN
            assertEquals(2, result.items[1].role) // USER
            assertEquals(0, result.items[2].role) // SYSTEM
        }

        @Test
        fun `deve converter Paged vazio corretamente`() {
            // Arrange
            val pagedUsers = Paged<User>(
                items = emptyList(),
                page = 0,
                pageSize = 10,
                totalItems = 0,
                totalPages = 0
            )

            // Act
            val result = UserMapper.toResponsePaged(pagedUsers)

            // Assert
            assertTrue(result.items.isEmpty())
            assertEquals(0, result.page)
            assertEquals(10, result.pageSize)
            assertEquals(0, result.totalItems)
            assertEquals(0, result.totalPages)
        }

        @Test
        fun `deve preservar dados de paginacao ao converter`() {
            // Arrange
            val users = List(25) { createUser(name = "User $it") }
            val pagedUsers = Paged(
                items = users,
                page = 2,
                pageSize = 25,
                totalItems = 100,
                totalPages = 4
            )

            // Act
            val result = UserMapper.toResponsePaged(pagedUsers)

            // Assert
            assertEquals(25, result.items.size)
            assertEquals(2, result.page)
            assertEquals(25, result.pageSize)
            assertEquals(100, result.totalItems)
            assertEquals(4, result.totalPages)
        }

        @Test
        fun `deve converter todos os campos de cada usuario`() {
            // Arrange
            val userId1 = UUID.randomUUID()
            val userId2 = UUID.randomUUID()
            val now = LocalDateTime.now()

            val users = listOf(
                createUser(
                    id = userId1,
                    name = "João",
                    email = "joao@example.com",
                    phone = "11111111111",
                    role = UserRole.ADMIN,
                    createdAt = now,
                    updatedAt = now.plusDays(1)
                ),
                createUser(
                    id = userId2,
                    name = "Maria",
                    email = "maria@example.com",
                    phone = "22222222222",
                    role = UserRole.USER,
                    createdAt = now,
                    updatedAt = null
                )
            )
            val pagedUsers = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 2,
                totalPages = 1
            )

            // Act
            val result = UserMapper.toResponsePaged(pagedUsers)

            // Assert
            assertEquals(userId1, result.items[0].id)
            assertEquals("João", result.items[0].name)
            assertEquals("joao@example.com", result.items[0].email)
            assertEquals("11111111111", result.items[0].phone)
            assertEquals(1, result.items[0].role)
            assertNotNull(result.items[0].updatedAt)

            assertEquals(userId2, result.items[1].id)
            assertEquals("Maria", result.items[1].name)
            assertEquals("maria@example.com", result.items[1].email)
            assertEquals("22222222222", result.items[1].phone)
            assertEquals(2, result.items[1].role)
            assertNull(result.items[1].updatedAt)
        }

        @Test
        fun `deve converter lista com um unico usuario`() {
            // Arrange
            val user = createUser(name = "Single User")
            val pagedUsers = Paged(
                items = listOf(user),
                page = 0,
                pageSize = 10,
                totalItems = 1,
                totalPages = 1
            )

            // Act
            val result = UserMapper.toResponsePaged(pagedUsers)

            // Assert
            assertEquals(1, result.items.size)
            assertEquals("Single User", result.items[0].name)
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

