package br.com.fiapx.fiapxuser.infrastructure.persistence.adapter

import br.com.fiapx.fiapxuser.domain.common.Paged
import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import br.com.fiapx.fiapxuser.infrastructure.persistence.mapper.UserEntityMapper
import br.com.fiapx.fiapxuser.infrastructure.persistence.repository.UserJpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserJpaRepositoryAdapter(
    private val springDataRepository: UserJpaRepository
) : UserRepository {
    override fun findPaged(
        page: Int,
        pageSize: Int
    ): Paged<User> {
        val pageable = PageRequest.of(page, pageSize)
        val categoriesPaged = springDataRepository.findPaged(pageable)

        return UserEntityMapper.toPagedDomain(categoriesPaged)
    }

    override fun findById(id: UUID): User? {
        return springDataRepository.findByIdAndNotDeleted(id)
            .map { UserEntityMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByIds(ids: List<UUID>): List<User> {
        return springDataRepository.findAllByIdAndNotDeleted(ids)
            .map { UserEntityMapper.toDomain(it) }
    }

    override fun save(entity: User): User {
        val saved = springDataRepository.save(
            UserEntityMapper.toEntity(entity)
        )
        return UserEntityMapper.toDomain(saved)
    }

    override fun deleteById(id: UUID): Boolean {
        springDataRepository.deleteById(id)

        val exists = springDataRepository.existsByIdAndNotDeleted(id)
        return !exists
    }
}