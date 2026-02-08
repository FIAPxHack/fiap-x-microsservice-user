package br.com.fiapx.fiapxuser.application.usecase.queries.getall

import br.com.fiapx.fiapxuser.domain.common.Paged
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@DisplayName("GetAllUsersUseCase - Testes Unitários")
class GetAllUsersUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: GetAllUsersUseCase

    @BeforeEach
    fun setup() {
        useCase = GetAllUsersUseCase(userRepository)
    }

    @Nested
    @DisplayName("Cenários de Sucesso")
    inner class SuccessScenarios {

        @Test
        fun `deve retornar lista paginada de usuarios quando existem registros`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 10)
            val users = listOf(
                createUser(name = "João Silva"),
                createUser(name = "Maria Santos"),
                createUser(name = "Pedro Oliveira")
            )
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 3,
                totalPages = 1
            )

            every { userRepository.findPaged(page = 0, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertEquals(3, result.items.size)
            assertEquals(0, result.page)
            assertEquals(10, result.pageSize)
            assertEquals(3, result.totalItems)
            assertEquals(1, result.totalPages)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 10) }
        }

        @Test
        fun `deve retornar lista vazia quando nao existem usuarios`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 10)
            val pagedResult = Paged<User>(
                items = emptyList(),
                page = 0,
                pageSize = 10,
                totalItems = 0,
                totalPages = 0
            )

            every { userRepository.findPaged(page = 0, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertNotNull(result)
            assertTrue(result.items.isEmpty())
            assertEquals(0, result.totalItems)
            assertEquals(0, result.totalPages)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 10) }
        }

        @Test
        fun `deve retornar segunda pagina de usuarios corretamente`() {
            // Arrange
            val query = GetAllUsersQuery(page = 1, pageSize = 5)
            val users = listOf(
                createUser(name = "Usuário 6"),
                createUser(name = "Usuário 7"),
                createUser(name = "Usuário 8")
            )
            val pagedResult = Paged(
                items = users,
                page = 1,
                pageSize = 5,
                totalItems = 8,
                totalPages = 2
            )

            every { userRepository.findPaged(page = 1, pageSize = 5) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(3, result.items.size)
            assertEquals(1, result.page)
            assertEquals(5, result.pageSize)
            assertEquals(8, result.totalItems)
            assertEquals(2, result.totalPages)
            verify(exactly = 1) { userRepository.findPaged(page = 1, pageSize = 5) }
        }

        @Test
        fun `deve usar valores default quando nao especificados`() {
            // Arrange
            val query = GetAllUsersQuery()
            val users = listOf(createUser())
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 1,
                totalPages = 1
            )

            every { userRepository.findPaged(page = 0, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(0, result.page)
            assertEquals(10, result.pageSize)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 10) }
        }

        @Test
        fun `deve retornar usuarios com todos os campos preenchidos`() {
            // Arrange
            val userId = UUID.randomUUID()
            val createdBy = UUID.randomUUID()
            val createdAt = LocalDateTime.now()
            val query = GetAllUsersQuery(page = 0, pageSize = 10)
            val user = createUser(
                id = userId,
                name = "Carlos Eduardo",
                email = "carlos@example.com",
                phone = "11987654321",
                role = UserRole.ADMIN,
                createdBy = createdBy,
                createdAt = createdAt
            )
            val pagedResult = Paged(
                items = listOf(user),
                page = 0,
                pageSize = 10,
                totalItems = 1,
                totalPages = 1
            )

            every { userRepository.findPaged(page = 0, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            val returnedUser = result.items.first()
            assertEquals(userId, returnedUser.id)
            assertEquals("Carlos Eduardo", returnedUser.name)
            assertEquals("carlos@example.com", returnedUser.email)
            assertEquals("11987654321", returnedUser.phone)
            assertEquals(UserRole.ADMIN, returnedUser.role)
            assertEquals(createdBy, returnedUser.createdBy)
            assertEquals(createdAt, returnedUser.createdAt)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 10) }
        }

        @Test
        fun `deve retornar usuarios com diferentes roles`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 10)
            val users = listOf(
                createUser(name = "Admin User", role = UserRole.ADMIN),
                createUser(name = "System User", role = UserRole.SYSTEM),
                createUser(name = "Regular User", role = UserRole.USER)
            )
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 3,
                totalPages = 1
            )

            every { userRepository.findPaged(page = 0, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(3, result.items.size)
            assertEquals(UserRole.ADMIN, result.items[0].role)
            assertEquals(UserRole.SYSTEM, result.items[1].role)
            assertEquals(UserRole.USER, result.items[2].role)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 10) }
        }

        @Test
        fun `deve retornar apenas usuarios nao deletados`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 10)
            val users = listOf(
                createUser(name = "User 1", deleted = false),
                createUser(name = "User 2", deleted = false)
            )
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 2,
                totalPages = 1
            )

            every { userRepository.findPaged(page = 0, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertTrue(result.items.all { !it.deleted })
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 10) }
        }

        @Test
        fun `deve retornar pageSize customizado corretamente`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 25)
            val users = List(25) { createUser(name = "User $it") }
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 25,
                totalItems = 100,
                totalPages = 4
            )

            every { userRepository.findPaged(page = 0, pageSize = 25) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(25, result.items.size)
            assertEquals(25, result.pageSize)
            assertEquals(100, result.totalItems)
            assertEquals(4, result.totalPages)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 25) }
        }
    }

    @Nested
    @DisplayName("Validação de Paginação")
    inner class PaginationValidation {

        @Test
        fun `deve calcular totalPages corretamente quando divisao exata`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 10)
            val users = List(10) { createUser() }
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 100,
                totalPages = 10
            )

            every { userRepository.findPaged(page = 0, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(10, result.totalPages)
            assertEquals(100, result.totalItems)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 10) }
        }

        @Test
        fun `deve calcular totalPages corretamente quando divisao com resto`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 10)
            val users = List(10) { createUser() }
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 10,
                totalItems = 95,
                totalPages = 10
            )

            every { userRepository.findPaged(page = 0, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(10, result.totalPages)
            assertEquals(95, result.totalItems)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 10) }
        }

        @Test
        fun `deve retornar pagina com menos itens que pageSize na ultima pagina`() {
            // Arrange
            val query = GetAllUsersQuery(page = 2, pageSize = 10)
            val users = List(5) { createUser() }
            val pagedResult = Paged(
                items = users,
                page = 2,
                pageSize = 10,
                totalItems = 25,
                totalPages = 3
            )

            every { userRepository.findPaged(page = 2, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(5, result.items.size)
            assertEquals(2, result.page)
            assertEquals(25, result.totalItems)
            assertEquals(3, result.totalPages)
            verify(exactly = 1) { userRepository.findPaged(page = 2, pageSize = 10) }
        }

        @Test
        fun `deve retornar lista vazia para pagina alem do total`() {
            // Arrange
            val query = GetAllUsersQuery(page = 10, pageSize = 10)
            val pagedResult = Paged<User>(
                items = emptyList(),
                page = 10,
                pageSize = 10,
                totalItems = 50,
                totalPages = 5
            )

            every { userRepository.findPaged(page = 10, pageSize = 10) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertTrue(result.items.isEmpty())
            assertEquals(10, result.page)
            verify(exactly = 1) { userRepository.findPaged(page = 10, pageSize = 10) }
        }

        @Test
        fun `deve permitir pageSize de 1`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 1)
            val users = listOf(createUser())
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 1,
                totalItems = 10,
                totalPages = 10
            )

            every { userRepository.findPaged(page = 0, pageSize = 1) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(1, result.items.size)
            assertEquals(1, result.pageSize)
            assertEquals(10, result.totalPages)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 1) }
        }

        @Test
        fun `deve permitir pageSize grande`() {
            // Arrange
            val query = GetAllUsersQuery(page = 0, pageSize = 100)
            val users = List(50) { createUser() }
            val pagedResult = Paged(
                items = users,
                page = 0,
                pageSize = 100,
                totalItems = 50,
                totalPages = 1
            )

            every { userRepository.findPaged(page = 0, pageSize = 100) } returns pagedResult

            // Act
            val result = useCase.execute(query)

            // Assert
            assertEquals(50, result.items.size)
            assertEquals(100, result.pageSize)
            assertEquals(1, result.totalPages)
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 100) }
        }
    }

    @Nested
    @DisplayName("Múltiplas Chamadas")
    inner class MultipleCalls {

        @Test
        fun `deve permitir chamadas consecutivas com parametros diferentes`() {
            // Arrange
            val query1 = GetAllUsersQuery(page = 0, pageSize = 5)
            val query2 = GetAllUsersQuery(page = 1, pageSize = 5)

            val users1 = List(5) { createUser(name = "Page 0 User $it") }
            val users2 = List(5) { createUser(name = "Page 1 User $it") }

            val pagedResult1 = Paged(items = users1, page = 0, pageSize = 5, totalItems = 10, totalPages = 2)
            val pagedResult2 = Paged(items = users2, page = 1, pageSize = 5, totalItems = 10, totalPages = 2)

            every { userRepository.findPaged(page = 0, pageSize = 5) } returns pagedResult1
            every { userRepository.findPaged(page = 1, pageSize = 5) } returns pagedResult2

            // Act
            val result1 = useCase.execute(query1)
            val result2 = useCase.execute(query2)

            // Assert
            assertEquals(0, result1.page)
            assertEquals(1, result2.page)
            assertTrue(result1.items[0].name.contains("Page 0"))
            assertTrue(result2.items[0].name.contains("Page 1"))
            verify(exactly = 1) { userRepository.findPaged(page = 0, pageSize = 5) }
            verify(exactly = 1) { userRepository.findPaged(page = 1, pageSize = 5) }
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

