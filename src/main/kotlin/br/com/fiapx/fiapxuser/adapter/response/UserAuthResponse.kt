package br.com.fiapx.fiapxuser.adapter.response

import java.util.UUID

data class UserAuthResponse(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val role: String
)
