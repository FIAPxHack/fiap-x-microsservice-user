package br.com.fiapx.fiapxuser.application.usecase.commands.update

import java.time.LocalDate
import java.util.UUID

data class UpdateUserCommand(
    val id: UUID,
    val name: String,
    val email: String,
    val password: String,
    val birthDate: LocalDate,
    val phone: String,
    val updatedBy: UUID
)