package br.com.fiapx.fiapxuser.application.usecase.commands.update

import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {

    fun execute(command: UpdateUserCommand): User {
        val userFinded = userRepository.findById(command.id)
            ?: throw IllegalArgumentException("$PREFIX O usuário não encontrada")

        val updated = userFinded.copy(
            name = command.name,
            updatedBy = command.updatedBy
        )

        try {
            val userUpdated = userRepository.save(updated)
            logger.debug(
                "{} O usuário foi atualizada com sucesso, foi atualizado de [{}] para [{}]",
                PREFIX,
                userFinded.name,
                command.name
            )
            return userUpdated
        } catch (ex: DataAccessException) {
            logger.error("$PREFIX Falha ao tentar atualizar o usuário: ${command.name}")
            throw Exception("$PREFIX Erro ao atualizar entidade", ex)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val PREFIX = "[UPDATE_User_USE_CASE]"
    }
}