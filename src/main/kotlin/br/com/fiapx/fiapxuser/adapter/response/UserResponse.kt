package br.com.fiapx.fiapxuser.adapter.response

import java.time.LocalDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String,
    val role: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
