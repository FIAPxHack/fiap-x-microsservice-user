package br.com.fiapx.fiapxuser.adapter.controller

import br.com.fiapx.fiapxuser.adapter.mapper.UserMapper
import br.com.fiapx.fiapxuser.adapter.request.commands.create.CreateUserRequest
import br.com.fiapx.fiapxuser.adapter.request.commands.update.UpdateUserRequest
import br.com.fiapx.fiapxuser.adapter.response.UserResponse
import br.com.fiapx.fiapxuser.application.usecase.commands.create.CreateUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.commands.delete.DeleteUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.commands.update.UpdateUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.queries.getall.GetAllUsersQuery
import br.com.fiapx.fiapxuser.application.usecase.queries.getall.GetAllUsersUseCase
import br.com.fiapx.fiapxuser.application.usecase.queries.getbyid.GetUserByIdQuery
import br.com.fiapx.fiapxuser.application.usecase.queries.getbyid.GetUserByIdUseCase
import br.com.fiapx.fiapxuser.domain.common.Paged
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/users")
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase
) {
    @PostMapping
    fun create(
        @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val createdBy = UUID.randomUUID()
        val command = UserMapper.toCreateCommand(request, createdBy)
        val user = createUserUseCase.execute(command)

        return ResponseEntity.ok(UserMapper.toResponse(user))
    }

    @PutMapping
    fun update(
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val updatedBy = UUID.randomUUID()
        val command = UserMapper.toUpdateCommand(request, updatedBy)
        val user = updateUserUseCase.execute(command)

        return ResponseEntity.ok(UserMapper.toResponse(user))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        val deletedBy = UUID.randomUUID()
        val command = UserMapper.toDeleteCommand(id, deletedBy)
        deleteUserUseCase.execute(command)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<UserResponse> {
        val user = getUserByIdUseCase.execute(GetUserByIdQuery(id))
        return if (user != null) ResponseEntity.ok(UserMapper.toResponse(user)) else ResponseEntity.notFound()
            .build()
    }

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") pageSize: Int
    ): ResponseEntity<Paged<UserResponse>> {
        val users = getAllUsersUseCase.execute(GetAllUsersQuery(page, pageSize))
        return ResponseEntity.ok(UserMapper.toResponsePaged(users))
    }
}