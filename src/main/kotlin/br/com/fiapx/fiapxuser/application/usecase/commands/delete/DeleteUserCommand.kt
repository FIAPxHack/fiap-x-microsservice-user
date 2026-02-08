package br.com.fiapx.fiapxuser.application.usecase.commands.delete

import java.util.UUID

data class DeleteUserCommand(
    val id: UUID,
    val deletedBy: UUID
)
