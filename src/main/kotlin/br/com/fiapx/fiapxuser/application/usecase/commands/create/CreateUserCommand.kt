package br.com.fiapx.fiapxuser.application.usecase.commands.create

import java.time.LocalDate
import java.util.UUID

data class CreateUserCommand(
    val name: String,
    val email: String,
    val password: String,
    val birthDate: LocalDate,
    val phone: String,
    val role: Int,
    val createdBy: UUID
)