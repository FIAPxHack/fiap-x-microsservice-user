package br.com.fiapx.fiapxuser.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime
import java.util.UUID

@MappedSuperclass
abstract class BaseJpaEntity(
    @Id
    @Column(nullable = false, updatable = false, length = 36)
    val id: UUID,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false, updatable = false)
    val createdBy: UUID,

    @Column
    var updatedAt: LocalDateTime? = null,

    @Column
    var updatedBy: UUID? = null,

    @Column(nullable = false)
    var deleted: Boolean = false
)