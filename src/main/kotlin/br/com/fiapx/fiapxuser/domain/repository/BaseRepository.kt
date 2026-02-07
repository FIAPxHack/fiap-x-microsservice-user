package br.com.fiapx.fiapxuser.domain.repository

import br.com.fiapx.fiapxuser.domain.common.BaseEntity
import br.com.fiapx.fiapxuser.domain.common.Paged
import java.util.UUID

interface BaseRepository<T : BaseEntity> {

    fun findById(id: UUID): T?
    fun findPaged(page: Int, pageSize: Int): Paged<T>
    fun findByIds(ids: List<UUID>): List<T>
    fun save(entity: T): T
    fun deleteById(id: UUID): Boolean
}