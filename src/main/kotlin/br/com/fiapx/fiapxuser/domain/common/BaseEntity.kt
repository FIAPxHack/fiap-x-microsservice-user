package br.com.fiapx.fiapxuser.domain.common

import java.time.LocalDateTime
import java.util.UUID

abstract class BaseEntity(
    open val id: UUID,
    open val createdAt: LocalDateTime,
    open val createdBy: UUID,
    open val updatedAt: LocalDateTime?,
    open val updatedBy: UUID?,
    open val deleted: Boolean
)
