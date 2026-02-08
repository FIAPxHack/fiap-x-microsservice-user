package br.com.fiapx.fiapxuser.application.usecase.commands.delete

import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(command: DeleteUserCommand) {
        val userFinded = userRepository.findById(command.id)
        if(userFinded == null) {
            logger.warn("$PREFIX O usuário com id ${command.id} não encontrado.")
            throw IllegalArgumentException("$PREFIX O usuário com id ${command.id} não encontrada.")
        }

        val deleted = userFinded.copy(
            deleted = true,
            updatedBy = command.deletedBy
        )

        try {
            userRepository.save(deleted)
            logger.debug("$PREFIX O usuário foi deletada com sucesso: ${userFinded.name}")
        } catch (ex: DataAccessException) {
            logger.error("$PREFIX Falha ao tentar deletar o usuário: ${userFinded.name}")
            throw Exception("$PREFIX Falha ao tentar deletar o usuário", ex)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val PREFIX = "[DELETE_USER_USE_CASE]"
    }
}