package br.com.fiapx.fiapxuser.application.usecase.queries.getbyemail

import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException

class GetUserByEmailUseCase(
    private val userRepository: UserRepository
) {

    fun execute(query: GetUserByEmailQuery): User? {
        try {
            val userFinded = userRepository.findByEmail(query.email)
            if (userFinded != null) {
                logger.debug(
                    "{} Usuário encontrado - nome: [{}]|email: [{}]",
                    PREFIX, userFinded.name, userFinded.email
                )
            } else {
                logger.debug("{} O usuário não encontrado com email: {}", PREFIX, query.email)
            }

            return userFinded

        } catch (ex: DataAccessException) {
            logger.error("$PREFIX Falha ao tentar encontrar o usuário do email ${query.email}")
            throw Exception("$PREFIX Falha ao tentar encontrar o usuário do email ${query.email}", ex)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val PREFIX = "[GET_BY_EMAIL_USER_USE_CASE]"
    }
}
