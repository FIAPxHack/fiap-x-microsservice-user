package br.com.fiapx.fiapxuser.application.usecase.queries.getall

import br.com.fiapx.fiapxuser.domain.common.Paged
import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import org.slf4j.LoggerFactory

class GetAllUsersUseCase(
    private val userRepository: UserRepository
) {

    fun execute(query: GetAllUsersQuery): Paged<User> {
        val usersListFinded = userRepository.findPaged(
            page = query.page,
            pageSize = query.pageSize
        )

        if(usersListFinded.items.isNotEmpty()) {
            logger.debug("$PREFIX Foi/Foram encontrado(s) [${usersListFinded.totalItems}] usuário(s) na base de dados.")
        } else {
            logger.debug("$PREFIX Não foi encontrado nenhum usuário na base de dados.")
        }

        return usersListFinded
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val PREFIX = "[GET_ALL_USER_USE_CASE]"
    }
}
