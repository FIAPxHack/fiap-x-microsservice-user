package br.com.fiapx.fiapxuser.infrastructure.config

import br.com.fiapx.fiapxuser.application.usecase.commands.create.CreateUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.commands.delete.DeleteUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.queries.getbyid.GetUserByIdUseCase
import br.com.fiapx.fiapxuser.application.usecase.commands.update.UpdateUserUseCase
import br.com.fiapx.fiapxuser.application.usecase.queries.getall.GetAllUsersUseCase
import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserUseCaseConfig(
    private val userRepository: UserRepository
) {
    @Bean
    fun createUserUseCase(): CreateUserUseCase {
        return CreateUserUseCase(userRepository)
    }

    @Bean
    fun updateUserUseCase(): UpdateUserUseCase {
        return UpdateUserUseCase(userRepository)
    }

    @Bean
    fun deleteUserUseCase(): DeleteUserUseCase {
        return DeleteUserUseCase(userRepository)
    }

    @Bean
    fun getUserByIdUseCase(): GetUserByIdUseCase {
        return GetUserByIdUseCase(userRepository)
    }

    @Bean
    fun getAllUsersUseCase(): GetAllUsersUseCase {
        return GetAllUsersUseCase(userRepository)
    }
}