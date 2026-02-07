package br.com.fiapx.fiapxuser.infrastructure.persistence.mapper

import br.com.fiapx.fiapxuser.domain.common.Paged
import br.com.fiapx.fiapxuser.domain.enums.UserRole.Companion.fromCode
import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.infrastructure.persistence.entity.UserJpaEntity
import org.springframework.data.domain.Page

object UserEntityMapper {

    fun toEntity(domain: User): UserJpaEntity =
        UserJpaEntity(
            id = domain.id,
            name = domain.name,
            email = domain.email,
            password = domain.password,
            birthDate = domain.birthDate,
            phone = domain.phone,
            role = domain.role.code,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            createdBy = domain.createdBy,
            updatedBy = domain.updatedBy,
            deleted = domain.deleted,
        )

    fun toDomain(jpa: UserJpaEntity): User =
        User(
            id = jpa.id,
            name = jpa.name,
            email = jpa.email,
            password = jpa.password,
            birthDate = jpa.birthDate,
            phone = jpa.phone,
            role = fromCode(jpa.role),
            createdAt = jpa.createdAt,
            updatedAt = jpa.updatedAt,
            createdBy = jpa.createdBy,
            updatedBy = jpa.updatedBy,
            deleted = jpa.deleted
        )

    fun toPagedDomain(jpa: Page<UserJpaEntity>): Paged<User> =
        Paged(
            items = jpa.content.map { toDomain(it) },
            page = jpa.number,
            pageSize = jpa.size,
            totalItems = jpa.totalElements,
            totalPages = jpa.totalPages
        )
}