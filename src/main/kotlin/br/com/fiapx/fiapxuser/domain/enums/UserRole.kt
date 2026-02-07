package br.com.fiapx.fiapxuser.domain.enums

enum class UserRole(val code: Int) {
    SYSTEM(0),
    ADMIN(1),
    USER(2);

    companion object {
        fun fromCode(code: Int): UserRole =
            UserRole.entries.find { it.code == code }
                ?: throw IllegalArgumentException("O tipo de usário [$code] não foi encontrado.")
    }
}