package br.com.fiapx.fiapxuser.application.usecase.queries.getall

data class GetAllUsersQuery(
    val page: Int = 0,
    val pageSize: Int = 10
)
