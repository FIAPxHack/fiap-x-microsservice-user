package br.com.fiapx.fiapxuser.adapter.controller

import br.com.fiapx.fiapxuser.adapter.request.commands.create.CreateUserRequest
import br.com.fiapx.fiapxuser.adapter.request.commands.update.UpdateUserRequest
import br.com.fiapx.fiapxuser.application.usecase.commands.create.CreateUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.commands.delete.DeleteUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.commands.update.UpdateUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.queries.getall.GetAllUsersQuery
import br.com.fiapx.fiapxuser.application.usecase.queries.getall.GetAllUsersUseCase
import br.com.fiapx.fiapxuser.application.usecase.queries.getbyid.GetUserByIdQuery
import br.com.fiapx.fiapxuser.application.usecase.queries.getbyid.GetUserByIdUseCase
import br.com.fiapx.fiapxuser.domain.common.Paged
import br.com.fiapx.fiapxuser.domain.enums.UserRole
import br.com.fiapx.fiapxuser.domain.model.User
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("UserController - Testes Unitários")
class UserControllerTest {

    private val createUserUseCase = mockk<CreateUserUseCase>()
    private val updateUserUseCase = mockk<UpdateUserUseCase>()
    private val deleteUserUseCase = mockk<DeleteUserUseCase>()
    private val getUserByIdUseCase = mockk<GetUserByIdUseCase>()
    private val getAllUsersUseCase = mockk<GetAllUsersUseCase>()

    private lateinit var controller: UserController

    @BeforeEach
    fun setup() {
        controller = UserController(
            createUserUseCase = createUserUseCase,
            updateUserUseCase = updateUserUseCase,
            deleteUserUseCase = deleteUserUseCase,
            getUserByIdUseCase = getUserByIdUseCase,
            getAllUsersUseCase = getAllUsersUseCase
        )
    }

    @Nested
    @DisplayName("POST /api/users - Create")
    inner class CreateTests {

        @Test
        fun `deve criar usuario com sucesso e retornar 200 OK`() {
            // Arrange
            val request = CreateUserRequest(
                name = "João Silva",
                email = "joao@example.com",
                password = "senha123",
                birthDate = LocalDate.of(1990, 1, 1),
                phone = "11987654321",
                role = 1
            )
            val createdUser = createUser(
                name = "João Silva",
                email = "joao@example.com",
                role = UserRole.ADMIN
            )

            every { createUserUseCase.execute(any()) } returns createdUser

            // Act
            val response = controller.create(request)

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            assertEquals("João Silva", response.body?.name)
            assertEquals("joao@example.com", response.body?.email)
            verify(exactly = 1) { createUserUseCase.execute(any()) }
        }

        @Test
        fun `deve passar command correto para o use case`() {
            // Arrange
            val request = CreateUserRequest(
                name = "Maria Santos",
                email = "maria@example.com",
                password = "senha456",
                birthDate = LocalDate.of(1985, 5, 15),
                phone = "11976543210",
                role = 2
            )
            val createdUser = createUser()
            val commandSlot = slot<br.com.fiapx.fiapxuser.application.usecase.commands.create.CreateUserCommand>()

            every { createUserUseCase.execute(capture(commandSlot)) } returns createdUser

            // Act
            controller.create(request)

            // Assert
            val capturedCommand = commandSlot.captured
            assertEquals("Maria Santos", capturedCommand.name)
            assertEquals("maria@example.com", capturedCommand.email)
            assertEquals("senha456", capturedCommand.password)
            assertEquals(LocalDate.of(1985, 5, 15), capturedCommand.birthDate)
            assertEquals("11976543210", capturedCommand.phone)
            assertEquals(2, capturedCommand.role)
            assertNotNull(capturedCommand.createdBy)
            verify(exactly = 1) { createUserUseCase.execute(any()) }
        }

        @Test
        fun `deve gerar createdBy automaticamente`() {
            // Arrange
            val request = createValidCreateRequest()
            val createdUser = createUser()
            val commandSlot = slot<br.com.fiapx.fiapxuser.application.usecase.commands.create.CreateUserCommand>()

            every { createUserUseCase.execute(capture(commandSlot)) } returns createdUser

            // Act
            controller.create(request)

            // Assert
            assertNotNull(commandSlot.captured.createdBy)
            verify(exactly = 1) { createUserUseCase.execute(any()) }
        }

        @Test
        fun `deve retornar response com todos os campos corretos`() {
            // Arrange
            val request = createValidCreateRequest()
            val userId = UUID.randomUUID()
            val createdAt = LocalDateTime.now()
            val createdUser = createUser(
                id = userId,
                name = "Test User",
                email = "test@example.com",
                phone = "11999999999",
                role = UserRole.USER,
                createdAt = createdAt
            )

            every { createUserUseCase.execute(any()) } returns createdUser

            // Act
            val response = controller.create(request)

            // Assert
            val body = response.body!!
            assertEquals(userId, body.id)
            assertEquals("Test User", body.name)
            assertEquals("test@example.com", body.email)
            assertEquals("11999999999", body.phone)
            assertEquals(2, body.role) // USER.code = 2
            assertEquals(createdAt, body.createdAt)
            verify(exactly = 1) { createUserUseCase.execute(any()) }
        }
    }

    @Nested
    @DisplayName("PUT /api/users - Update")
    inner class UpdateTests {

        @Test
        fun `deve atualizar usuario com sucesso e retornar 200 OK`() {
            // Arrange
            val userId = UUID.randomUUID()
            val request = UpdateUserRequest(
                id = userId,
                name = "Nome Atualizado",
                email = "atualizado@example.com",
                password = "newpass",
                birthDate = LocalDate.of(1990, 1, 1),
                phone = "11987654321"
            )
            val updatedUser = createUser(
                id = userId,
                name = "Nome Atualizado"
            )

            every { updateUserUseCase.execute(any()) } returns updatedUser

            // Act
            val response = controller.update(request)

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            assertEquals("Nome Atualizado", response.body?.name)
            verify(exactly = 1) { updateUserUseCase.execute(any()) }
        }

        @Test
        fun `deve passar command correto para o use case`() {
            // Arrange
            val userId = UUID.randomUUID()
            val request = UpdateUserRequest(
                id = userId,
                name = "Pedro Oliveira",
                email = "pedro@example.com",
                password = "pass123",
                birthDate = LocalDate.of(1992, 3, 20),
                phone = "11965432109"
            )
            val updatedUser = createUser()
            val commandSlot = slot<br.com.fiapx.fiapxuser.application.usecase.commands.update.UpdateUserCommand>()

            every { updateUserUseCase.execute(capture(commandSlot)) } returns updatedUser

            // Act
            controller.update(request)

            // Assert
            val capturedCommand = commandSlot.captured
            assertEquals(userId, capturedCommand.id)
            assertEquals("Pedro Oliveira", capturedCommand.name)
            assertEquals("pedro@example.com", capturedCommand.email)
            assertEquals("pass123", capturedCommand.password)
            assertEquals(LocalDate.of(1992, 3, 20), capturedCommand.birthDate)
            assertEquals("11965432109", capturedCommand.phone)
            assertNotNull(capturedCommand.updatedBy)
            verify(exactly = 1) { updateUserUseCase.execute(any()) }
        }

        @Test
        fun `deve gerar updatedBy automaticamente`() {
            // Arrange
            val request = createValidUpdateRequest()
            val updatedUser = createUser()
            val commandSlot = slot<br.com.fiapx.fiapxuser.application.usecase.commands.update.UpdateUserCommand>()

            every { updateUserUseCase.execute(capture(commandSlot)) } returns updatedUser

            // Act
            controller.update(request)

            // Assert
            assertNotNull(commandSlot.captured.updatedBy)
            verify(exactly = 1) { updateUserUseCase.execute(any()) }
        }

        @Test
        fun `deve retornar response com dados atualizados`() {
            // Arrange
            val userId = UUID.randomUUID()
            val request = createValidUpdateRequest(id = userId)
            val now = LocalDateTime.now()
            val updatedUser = createUser(
                id = userId,
                name = "Updated Name",
                updatedAt = now
            )

            every { updateUserUseCase.execute(any()) } returns updatedUser

            // Act
            val response = controller.update(request)

            // Assert
            val body = response.body!!
            assertEquals(userId, body.id)
            assertEquals("Updated Name", body.name)
            assertEquals(now, body.updatedAt)
            verify(exactly = 1) { updateUserUseCase.execute(any()) }
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id} - Delete")
    inner class DeleteTests {

        @Test
        fun `deve deletar usuario com sucesso e retornar 204 NO CONTENT`() {
            // Arrange
            val userId = UUID.randomUUID()

            justRun { deleteUserUseCase.execute(any()) }

            // Act
            val response = controller.delete(userId)

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
            assertNull(response.body)
            verify(exactly = 1) { deleteUserUseCase.execute(any()) }
        }

        @Test
        fun `deve passar command correto para o use case`() {
            // Arrange
            val userId = UUID.randomUUID()
            val commandSlot = slot<br.com.fiapx.fiapxuser.application.usecase.commands.delete.DeleteUserCommand>()

            every { deleteUserUseCase.execute(capture(commandSlot)) } just runs

            // Act
            controller.delete(userId)

            // Assert
            val capturedCommand = commandSlot.captured
            assertEquals(userId, capturedCommand.id)
            assertNotNull(capturedCommand.deletedBy)
            verify(exactly = 1) { deleteUserUseCase.execute(any()) }
        }

        @Test
        fun `deve gerar deletedBy automaticamente`() {
            // Arrange
            val userId = UUID.randomUUID()
            val commandSlot = slot<br.com.fiapx.fiapxuser.application.usecase.commands.delete.DeleteUserCommand>()

            every { deleteUserUseCase.execute(capture(commandSlot)) } just runs

            // Act
            controller.delete(userId)

            // Assert
            assertNotNull(commandSlot.captured.deletedBy)
            verify(exactly = 1) { deleteUserUseCase.execute(any()) }
        }

        @Test
        fun `deve deletar usuarios com IDs diferentes`() {
            // Arrange
            val userId1 = UUID.randomUUID()
            val userId2 = UUID.randomUUID()

            justRun { deleteUserUseCase.execute(any()) }

            // Act
            controller.delete(userId1)
            controller.delete(userId2)

            // Assert
            verify(exactly = 2) { deleteUserUseCase.execute(any()) }
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id} - Get By ID")
    inner class GetByIdTests {

        @Test
        fun `deve retornar usuario quando encontrado e retornar 200 OK`() {
            // Arrange
            val userId = UUID.randomUUID()
            val user = createUser(
                id = userId,
                name = "João Silva",
                email = "joao@example.com"
            )

            every { getUserByIdUseCase.execute(GetUserByIdQuery(userId)) } returns user

            // Act
            val response = controller.getById(userId)

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            assertEquals(userId, response.body?.id)
            assertEquals("João Silva", response.body?.name)
            assertEquals("joao@example.com", response.body?.email)
            verify(exactly = 1) { getUserByIdUseCase.execute(GetUserByIdQuery(userId)) }
        }

        @Test
        fun `deve retornar 404 NOT FOUND quando usuario nao encontrado`() {
            // Arrange
            val userId = UUID.randomUUID()

            every { getUserByIdUseCase.execute(GetUserByIdQuery(userId)) } returns null

            // Act
            val response = controller.getById(userId)

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
            assertNull(response.body)
            verify(exactly = 1) { getUserByIdUseCase.execute(GetUserByIdQuery(userId)) }
        }

        @Test
        fun `deve passar query correto para o use case`() {
            // Arrange
            val userId = UUID.randomUUID()
            val user = createUser(id = userId)
            val querySlot = slot<GetUserByIdQuery>()

            every { getUserByIdUseCase.execute(capture(querySlot)) } returns user

            // Act
            controller.getById(userId)

            // Assert
            assertEquals(userId, querySlot.captured.id)
            verify(exactly = 1) { getUserByIdUseCase.execute(any()) }
        }

        @Test
        fun `deve retornar usuario com todos os campos preenchidos`() {
            // Arrange
            val userId = UUID.randomUUID()
            val createdAt = LocalDateTime.now()
            val updatedAt = LocalDateTime.now().plusDays(1)
            val user = createUser(
                id = userId,
                name = "Maria Santos",
                email = "maria@example.com",
                phone = "11976543210",
                role = UserRole.ADMIN,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            every { getUserByIdUseCase.execute(GetUserByIdQuery(userId)) } returns user

            // Act
            val response = controller.getById(userId)

            // Assert
            val body = response.body!!
            assertEquals(userId, body.id)
            assertEquals("Maria Santos", body.name)
            assertEquals("maria@example.com", body.email)
            assertEquals("11976543210", body.phone)
            assertEquals(1, body.role) // ADMIN.code = 1
            assertEquals(createdAt, body.createdAt)
            assertEquals(updatedAt, body.updatedAt)
            verify(exactly = 1) { getUserByIdUseCase.execute(any()) }
        }
    }

    @Nested
    @DisplayName("GET /api/users - Get All")
    inner class GetAllTests {

        @Test
        fun `deve retornar lista paginada de usuarios e retornar 200 OK`() {
            // Arrange
            val users = listOf(
                createUser(name = "User 1"),
                createUser(name = "User 2"),
                createUser(name = "User 3")
            )
            val pagedUsers = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 3,
                totalPages = 1
            )

            every { getAllUsersUseCase.execute(GetAllUsersQuery(0, 10)) } returns pagedUsers

            // Act
            val response = controller.getAll(page = 0, pageSize = 10)

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertNotNull(response.body)
            assertEquals(3, response.body?.items?.size)
            assertEquals(0, response.body?.page)
            assertEquals(10, response.body?.pageSize)
            assertEquals(3, response.body?.totalItems)
            assertEquals(1, response.body?.totalPages)
            verify(exactly = 1) { getAllUsersUseCase.execute(GetAllUsersQuery(0, 10)) }
        }

        @Test
        fun `deve usar parametros default quando nao fornecidos`() {
            // Arrange
            val pagedUsers = Paged<User>(
                items = emptyList(),
                page = 0,
                pageSize = 10,
                totalItems = 0,
                totalPages = 0
            )
            val querySlot = slot<GetAllUsersQuery>()

            every { getAllUsersUseCase.execute(capture(querySlot)) } returns pagedUsers

            // Act
            controller.getAll(page = 0, pageSize = 10)

            // Assert
            assertEquals(0, querySlot.captured.page)
            assertEquals(10, querySlot.captured.pageSize)
            verify(exactly = 1) { getAllUsersUseCase.execute(any()) }
        }

        @Test
        fun `deve retornar segunda pagina corretamente`() {
            // Arrange
            val users = listOf(createUser(name = "User Page 2"))
            val pagedUsers = Paged(
                items = users,
                page = 1,
                pageSize = 5,
                totalItems = 10,
                totalPages = 2
            )

            every { getAllUsersUseCase.execute(GetAllUsersQuery(1, 5)) } returns pagedUsers

            // Act
            val response = controller.getAll(page = 1, pageSize = 5)

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertEquals(1, response.body?.page)
            assertEquals(5, response.body?.pageSize)
            assertEquals(10, response.body?.totalItems)
            assertEquals(2, response.body?.totalPages)
            verify(exactly = 1) { getAllUsersUseCase.execute(GetAllUsersQuery(1, 5)) }
        }

        @Test
        fun `deve retornar lista vazia quando nao existem usuarios`() {
            // Arrange
            val pagedUsers = Paged<User>(
                items = emptyList(),
                page = 0,
                pageSize = 10,
                totalItems = 0,
                totalPages = 0
            )

            every { getAllUsersUseCase.execute(GetAllUsersQuery(0, 10)) } returns pagedUsers

            // Act
            val response = controller.getAll(page = 0, pageSize = 10)

            // Assert
            assertEquals(HttpStatus.OK, response.statusCode)
            assertTrue(response.body?.items?.isEmpty() ?: false)
            verify(exactly = 1) { getAllUsersUseCase.execute(any()) }
        }

        @Test
        fun `deve passar query correto para o use case com parametros customizados`() {
            // Arrange
            val pagedUsers = Paged<User>(
                items = emptyList(),
                page = 3,
                pageSize = 25,
                totalItems = 0,
                totalPages = 0
            )
            val querySlot = slot<GetAllUsersQuery>()

            every { getAllUsersUseCase.execute(capture(querySlot)) } returns pagedUsers

            // Act
            controller.getAll(page = 3, pageSize = 25)

            // Assert
            assertEquals(3, querySlot.captured.page)
            assertEquals(25, querySlot.captured.pageSize)
            verify(exactly = 1) { getAllUsersUseCase.execute(any()) }
        }

        @Test
        fun `deve converter todos os usuarios para response corretamente`() {
            // Arrange
            val users = listOf(
                createUser(name = "João", role = UserRole.ADMIN),
                createUser(name = "Maria", role = UserRole.USER),
                createUser(name = "Pedro", role = UserRole.SYSTEM)
            )
            val pagedUsers = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 3,
                totalPages = 1
            )

            every { getAllUsersUseCase.execute(any()) } returns pagedUsers

            // Act
            val response = controller.getAll(page = 0, pageSize = 10)

            // Assert
            val items = response.body?.items!!
            assertEquals("João", items[0].name)
            assertEquals(1, items[0].role) // ADMIN
            assertEquals("Maria", items[1].name)
            assertEquals(2, items[1].role) // USER
            assertEquals("Pedro", items[2].name)
            assertEquals(0, items[2].role) // SYSTEM
            verify(exactly = 1) { getAllUsersUseCase.execute(any()) }
        }
    }

    // Helpers
    private fun createValidCreateRequest() = CreateUserRequest(
        name = "Test User",
        email = "test@example.com",
        password = "password",
        birthDate = LocalDate.of(1990, 1, 1),
        phone = "11987654321",
        role = 1
    )

    private fun createValidUpdateRequest(id: UUID = UUID.randomUUID()) = UpdateUserRequest(
        id = id,
        name = "Test User",
        email = "test@example.com",
        password = "password",
        birthDate = LocalDate.of(1990, 1, 1),
        phone = "11987654321"
    )

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

