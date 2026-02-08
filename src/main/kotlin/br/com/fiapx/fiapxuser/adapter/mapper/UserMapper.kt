package br.com.fiapx.fiapxuser.adapter.mapper

import br.com.fiapx.fiapxuser.adapter.request.commands.create.CreateUserRequest
import br.com.fiapx.fiapxuser.adapter.request.commands.update.UpdateUserRequest
import br.com.fiapx.fiapxuser.adapter.response.UserResponse
import br.com.fiapx.fiapxuser.application.usecase.commands.create.CreateUserCommand
import br.com.fiapx.fiapxuser.application.usecase.commands.delete.DeleteUserCommand
import br.com.fiapx.fiapxuser.application.usecase.commands.update.UpdateUserCommand
import br.com.fiapx.fiapxuser.domain.common.Paged
import br.com.fiapx.fiapxuser.domain.model.User
import java.util.UUID

object UserMapper {
    fun toCreateCommand(request: CreateUserRequest, createdBy: UUID) =
        CreateUserCommand(
            name = request.name,
            email = request.email,
            password = request.password,
            birthDate = request.birthDate,
            phone = request.phone,
            role = request.role,
            createdBy = createdBy
        )

    fun toUpdateCommand(request: UpdateUserRequest, updatedBy: UUID) =
        UpdateUserCommand(
            id = request.id,
            name = request.name,
            email = request.email,
            password = request.password,
            birthDate = request.birthDate,
            phone = request.phone,
            updatedBy = updatedBy
        )

    fun toDeleteCommand(id: UUID, deletedBy: UUID) =
        DeleteUserCommand(
            id = id,
            deletedBy = deletedBy
        )

    fun toResponse(user: User): UserResponse =
        UserResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            phone = user.phone,
            role = user.role.code,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )

    fun toResponsePaged(categoriesPaged: Paged<User>): Paged<UserResponse> {
        val userResponses = categoriesPaged.items.map { toResponse(it) }
        return Paged(
            items = userResponses,
            page = categoriesPaged.page,
            pageSize = categoriesPaged.pageSize,
            totalItems = categoriesPaged.totalItems,
            totalPages = categoriesPaged.totalPages
        )
    }
}