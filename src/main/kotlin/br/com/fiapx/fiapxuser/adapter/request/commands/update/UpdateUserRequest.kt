package br.com.fiapx.fiapxuser.adapter.request.commands.update

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.time.LocalDate
import java.util.UUID

data class UpdateUserRequest(
    @field:NotNull val id: UUID,
    @field:NotBlank val name: String,
    @field:Email val email: String,
    @field:NotBlank val password: String,
    @field:NotNull @field:Past val birthDate: LocalDate,
    @field:NotBlank val phone: String
)