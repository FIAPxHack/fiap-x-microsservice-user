package br.com.fiapx.fiapxuser.domain.common

data class Paged<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int
)
