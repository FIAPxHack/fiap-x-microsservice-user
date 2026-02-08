package br.com.fiapx.fiapxuser.infrastructure.persistence.repository

import br.com.fiapx.fiapxuser.infrastructure.persistence.entity.UserJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {
    @Query("SELECT c FROM UserJpaEntity c WHERE c.deleted = false")
    fun findPaged(pageable: Pageable): Page<UserJpaEntity>

    @Query("SELECT c FROM UserJpaEntity c WHERE c.id = :id AND c.deleted = false")
    fun findByIdAndNotDeleted(id: UUID): Optional<UserJpaEntity>

    @Query("SELECT c FROM UserJpaEntity c WHERE c.id IN :ids AND c.deleted = false")
    fun findAllByIdAndNotDeleted(ids: List<UUID>): List<UserJpaEntity>

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM UserJpaEntity c WHERE c.id = :id AND c.deleted = false")
    fun existsByIdAndNotDeleted(id: UUID): Boolean
}