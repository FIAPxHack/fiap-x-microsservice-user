package br.com.fiapx.fiapxuser.bdd.steps

import br.com.fiapx.fiapxuser.application.usecase.commands.create.CreateUserCommand
import br.com.fiapx.fiapxuser.application.usecase.commands.create.CreateUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.commands.delete.DeleteUserCommand
import br.com.fiapx.fiapxuser.application.usecase.commands.delete.DeleteUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.commands.update.UpdateUserCommand
import br.com.fiapx.fiapxuser.application.usecase.commands.update.UpdateUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.queries.getall.GetAllUsersQuery
import br.com.fiapx.fiapxuser.application.usecase.queries.getall.GetAllUsersUseCase
import br.com.fiapx.fiapxuser.application.usecase.queries.getbyid.GetUserByIdQuery
import br.com.fiapx.fiapxuser.application.usecase.queries.getbyid.GetUserByIdUseCase
import br.com.fiapx.fiapxuser.domain.common.Paged
import br.com.fiapx.fiapxuser.domain.enums.UserRole
import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.infrastructure.persistence.repository.UserJpaRepository
import io.cucumber.datatable.DataTable
import io.cucumber.java.Before
import io.cucumber.java.pt.Dado
import io.cucumber.java.pt.Então
import io.cucumber.java.pt.Quando
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import java.util.*

class UserManagementSteps {

    @Autowired
    private lateinit var createUserUseCase: CreateUserUseCase

    @Autowired
    private lateinit var getUserByIdUseCase: GetUserByIdUseCase

    @Autowired
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase

    @Autowired
    private lateinit var updateUserUseCase: UpdateUserUseCase

    @Autowired
    private lateinit var deleteUserUseCase: DeleteUserUseCase

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    private var userCommand: CreateUserCommand? = null
    private var updateCommand: UpdateUserCommand? = null
    private var createdUser: User? = null
    private var foundUser: User? = null
    private var pagedUsers: Paged<User>? = null
    private var userId: UUID? = null
    private var exception: Exception? = null

    @Before
    fun setup() {
        userCommand = null
        updateCommand = null
        createdUser = null
        foundUser = null
        pagedUsers = null
        userId = null
        exception = null
    }

    @Dado("que o banco de dados está limpo")
    fun limparBancoDeDados() {
        userJpaRepository.deleteAll()
    }

    @Dado("que eu tenho os seguintes dados de usuário:")
    fun prepararDadosUsuario(dataTable: DataTable) {
        val data = dataTable.asMap()
        val createdBy = UUID.randomUUID()

        userCommand = CreateUserCommand(
            name = data["nome"]!!,
            email = data["email"]!!,
            password = data["senha"]!!,
            birthDate = LocalDate.parse(data["dataNascimento"]),
            phone = data["telefone"]!!,
            role = data["role"]!!.toInt(),
            createdBy = createdBy
        )
    }

    @Quando("eu criar o usuário")
    fun criarUsuario() {
        try {
            createdUser = createUserUseCase.execute(userCommand!!)
        } catch (e: Exception) {
            exception = e
        }
    }

    @Então("o usuário deve ser criado com sucesso")
    fun validarUsuarioCriado() {
        assertNotNull(createdUser)
        assertNull(exception)
    }

    @Então("o usuário deve ter um ID gerado")
    fun validarIdGerado() {
        assertNotNull(createdUser?.id)
    }

    @Então("o usuário deve ter o nome {string}")
    fun validarNomeUsuario(nomeEsperado: String) {
        assertEquals(nomeEsperado, createdUser?.name ?: foundUser?.name)
    }

    @Então("o usuário deve ter o email {string}")
    fun validarEmailUsuario(emailEsperado: String) {
        assertEquals(emailEsperado, createdUser?.email ?: foundUser?.email)
    }

    @Dado("que existe um usuário cadastrado com:")
    fun criarUsuarioPreExistente(dataTable: DataTable) {
        val data = dataTable.asMap()
        val createdBy = UUID.randomUUID()

        val command = CreateUserCommand(
            name = data["nome"]!!,
            email = data["email"]!!,
            password = data["senha"]!!,
            birthDate = LocalDate.parse(data["dataNascimento"]),
            phone = data["telefone"]!!,
            role = data["role"]!!.toInt(),
            createdBy = createdBy
        )

        createdUser = createUserUseCase.execute(command)
        userId = createdUser?.id
    }

    @Quando("eu buscar o usuário por ID")
    fun buscarUsuarioPorId() {
        try {
            foundUser = getUserByIdUseCase.execute(GetUserByIdQuery(userId!!))
        } catch (e: Exception) {
            exception = e
        }
    }

    @Então("o usuário deve ser encontrado")
    fun validarUsuarioEncontrado() {
        assertNotNull(foundUser)
        assertNull(exception)
    }

    @Quando("eu buscar um usuário com ID inexistente")
    fun buscarUsuarioInexistente() {
        userId = UUID.randomUUID()
        foundUser = getUserByIdUseCase.execute(GetUserByIdQuery(userId!!))
    }

    @Então("o usuário não deve ser encontrado")
    fun validarUsuarioNaoEncontrado() {
        assertNull(foundUser)
    }

    @Dado("eu tenho os novos dados:")
    fun prepararDadosAtualizacao(dataTable: DataTable) {
        val data = dataTable.asMap()
        val updatedBy = UUID.randomUUID()

        updateCommand = UpdateUserCommand(
            id = userId ?: createdUser?.id!!,
            name = data["nome"]!!,
            email = createdUser?.email ?: "email@example.com",
            password = createdUser?.password ?: "senha123",
            birthDate = createdUser?.birthDate ?: LocalDate.now(),
            phone = createdUser?.phone ?: "11999999999",
            updatedBy = updatedBy
        )
    }

    @Quando("eu atualizar o usuário")
    fun atualizarUsuario() {
        try {
            createdUser = updateUserUseCase.execute(updateCommand!!)
        } catch (e: Exception) {
            exception = e
        }
    }

    @Então("o usuário deve ser atualizado com sucesso")
    fun validarUsuarioAtualizado() {
        assertNotNull(createdUser)
        assertNull(exception)
    }

    @Quando("eu deletar o usuário")
    fun deletarUsuario() {
        try {
            val deletedBy = UUID.randomUUID()
            deleteUserUseCase.execute(DeleteUserCommand(userId!!, deletedBy))
            foundUser = getUserByIdUseCase.execute(GetUserByIdQuery(userId!!))
        } catch (e: Exception) {
            exception = e
        }
    }

    @Então("o usuário deve ser marcado como deletado")
    fun validarUsuarioDeletado() {
        assertNotNull(foundUser)
    }

    @Então("o usuário deve ter a flag deleted como true")
    fun validarFlagDeleted() {
        assertTrue(foundUser?.deleted ?: false)
    }

    @Dado("que existem os seguintes usuários cadastrados:")
    fun criarMultiplosUsuarios(dataTable: DataTable) {
        val createdBy = UUID.randomUUID()

        dataTable.asMaps().forEach { row ->
            val command = CreateUserCommand(
                name = row["nome"]!!,
                email = row["email"]!!,
                password = row["senha"]!!,
                birthDate = LocalDate.parse(row["dataNascimento"]),
                phone = row["telefone"]!!,
                role = row["role"]!!.toInt(),
                createdBy = createdBy
            )
            createUserUseCase.execute(command)
        }
    }

    @Quando("eu listar os usuários da página {int} com tamanho {int}")
    fun listarUsuariosPaginados(page: Int, pageSize: Int) {
        pagedUsers = getAllUsersUseCase.execute(GetAllUsersQuery(page, pageSize))
    }

    @Então("devo receber {int} usuários")
    fun validarQuantidadeUsuarios(quantidade: Int) {
        assertEquals(quantidade, pagedUsers?.items?.size)
    }

    @Então("o total de itens deve ser {int}")
    fun validarTotalItens(total: Int) {
        assertEquals(total.toLong(), pagedUsers?.totalItems)
    }

    @Então("o número de páginas deve ser {int}")
    fun validarNumeroPaginas(paginas: Int) {
        assertEquals(paginas, pagedUsers?.totalPages)
    }

    @Então("o usuário deve ter o role ADMIN")
    fun validarRoleAdmin() {
        assertEquals(UserRole.ADMIN, createdUser?.role)
    }

    @Então("o usuário deve ter o role SYSTEM")
    fun validarRoleSystem() {
        assertEquals(UserRole.SYSTEM, createdUser?.role)
    }

    @Dado("que eu tenho um ID de usuário inexistente")
    fun prepararIdInexistente() {
        userId = UUID.randomUUID()
    }

    @Quando("eu tentar atualizar o usuário")
    fun tentarAtualizarUsuario() {
        try {
            updateUserUseCase.execute(updateCommand!!)
        } catch (e: Exception) {
            exception = e
        }
    }

    @Então("deve ocorrer um erro de usuário não encontrado")
    fun validarErroUsuarioNaoEncontrado() {
        assertNotNull(exception)
        assertTrue(exception is IllegalArgumentException)
        assertTrue(exception?.message?.contains("não encontrad") ?: false)
    }

    @Quando("eu tentar deletar o usuário")
    fun tentarDeletarUsuario() {
        try {
            val deletedBy = UUID.randomUUID()
            deleteUserUseCase.execute(DeleteUserCommand(userId!!, deletedBy))
        } catch (e: Exception) {
            exception = e
        }
    }
}

