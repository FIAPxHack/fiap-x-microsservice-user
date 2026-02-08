package br.com.fiapx.fiapxuser.domain.model

import br.com.fiapx.fiapxuser.domain.common.BaseEntity
import br.com.fiapx.fiapxuser.domain.enums.UserRole
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class User(
    override val id: UUID,
    override val createdBy: UUID,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    override val updatedBy: UUID? = null,
    override val updatedAt: LocalDateTime? = null,
    override val deleted: Boolean = false,
    val name: String,
    val email: String,
    val password: String,
    val birthDate: LocalDate,
    val phone: String,
    val role: UserRole,
) : BaseEntity(
    id = id,
    createdBy = createdBy,
    createdAt = createdAt,
    updatedBy = updatedBy,
    updatedAt = updatedAt,
    deleted = deleted
)