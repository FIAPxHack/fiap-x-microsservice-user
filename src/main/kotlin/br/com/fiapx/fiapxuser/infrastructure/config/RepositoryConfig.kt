package br.com.fiapx.fiapxuser.infrastructure.config

import br.com.fiapx.fiapxuser.domain.repository.UserRepository
import br.com.fiapx.fiapxuser.infrastructure.persistence.adapter.UserJpaRepositoryAdapter
import br.com.fiapx.fiapxuser.infrastructure.persistence.repository.UserJpaRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfig {
    
    @Bean
    fun userRepository(
        springRepo: UserJpaRepository
    ): UserRepository = UserJpaRepositoryAdapter(springRepo)
}
