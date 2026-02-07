package br.com.fiapx.fiapxuser.application.usecase.queries.getbyid

import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException

class GetUserByIdUseCase(
    private val userRepository: UserRepository
) {

    fun execute(query: GetUserByIdQuery): User? {
        try {
            val userFinded = userRepository.findById(query.id)
            if (userFinded != null) {
                logger.debug(
                    "{} Usuário encontrado - nome: [{}]|email: [{}]",
                    PREFIX, userFinded.name, userFinded.email
                )
            } else {
                logger.debug("{} O usuário não encontrado com id: {}", PREFIX, query.id)
            }

            return userFinded

        } catch (ex: DataAccessException) {
            logger.error("$PREFIX Falha ao tentar encontrar o usuário do id ${query.id}")
            throw Exception("$PREFIX Falha ao tentar encontrar o usuário do id ${query.id}", ex)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val PREFIX = "[GET_BY_ID_USER_USE_CASE]"
    }
}
