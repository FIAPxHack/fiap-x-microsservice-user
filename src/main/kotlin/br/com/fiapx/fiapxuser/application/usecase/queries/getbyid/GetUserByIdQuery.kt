package br.com.fiapx.fiapxuser.application.usecase.queries.getbyid

import java.util.UUID

data class GetUserByIdQuery(
    val id: UUID
)