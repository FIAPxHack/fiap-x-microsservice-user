package br.com.fiapx.fiapxuser.application.usecase.commands.create

import br.com.fiapx.fiapxuser.domain.enums.UserRole.Companion.fromCode
import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import java.time.LocalDateTime
import java.util.UUID

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(command: CreateUserCommand): User {
        val user = User(
            id = UUID.randomUUID(),
            name = command.name,
            email = command.email,
            password = command.password,
            birthDate = command.birthDate,
            phone = command.phone,
            role = fromCode(command.role),
            createdBy = command.createdBy,
            createdAt = LocalDateTime.now(),
        )

        try {
            logger.debug("$PREFIX O usuário foi criada com sucesso com name: ${command.name}")
            return userRepository.save(user)
        } catch (ex: DataAccessException) {
            logger.error("$PREFIX Falha ao tentar criar o usuário: ${command.name}")
            throw Exception("$PREFIX Falha ao tentar criar o usuário", ex)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val PREFIX = "[CREATE_USER_USE_CASE]"
    }
}