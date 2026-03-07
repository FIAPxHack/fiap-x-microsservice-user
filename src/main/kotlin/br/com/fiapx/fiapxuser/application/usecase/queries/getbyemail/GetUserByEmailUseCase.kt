package br.com.fiapx.fiapxuser.application.usecase.queries.getbyemail

import br.com.fiapx.fiapxuser.domain.model.User
import br.com.fiapx.fiapxuser.domain.repository.UserRepository

class GetUserByEmailUseCase(private val userRepository: UserRepository) {
    fun execute(query: GetUserByEmailQuery): User? {
        return userRepository.findByEmail(query.email)
    }
}
