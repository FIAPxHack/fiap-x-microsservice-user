package br.com.fiapx.fiapxuser.infrastructure.config

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MetricsConfig {

    @Bean
    fun userMetrics(meterRegistry: MeterRegistry): UserMetrics = UserMetrics(meterRegistry)
}

class UserMetrics(
    meterRegistry: MeterRegistry
) {
    val createSuccessCounter: Counter = Counter.builder("user_create_requests_total")
        .description("Total de criações de usuário com sucesso")
        .tag("outcome", "success")
        .register(meterRegistry)

    val createFailureCounter: Counter = Counter.builder("user_create_requests_total")
        .description("Total de criações de usuário com falha")
        .tag("outcome", "failure")
        .register(meterRegistry)

    val createTimer: Timer = Timer.builder("user_create_duration")
        .description("Tempo de execução da criação de usuário")
        .register(meterRegistry)

    val updateSuccessCounter: Counter = Counter.builder("user_update_requests_total")
        .description("Total de atualizações de usuário com sucesso")
        .tag("outcome", "success")
        .register(meterRegistry)

    val updateFailureCounter: Counter = Counter.builder("user_update_requests_total")
        .description("Total de atualizações de usuário com falha")
        .tag("outcome", "failure")
        .register(meterRegistry)

    val updateTimer: Timer = Timer.builder("user_update_duration")
        .description("Tempo de execução da atualização de usuário")
        .register(meterRegistry)

    val deleteSuccessCounter: Counter = Counter.builder("user_delete_requests_total")
        .description("Total de exclusões de usuário com sucesso")
        .tag("outcome", "success")
        .register(meterRegistry)

    val deleteFailureCounter: Counter = Counter.builder("user_delete_requests_total")
        .description("Total de exclusões de usuário com falha")
        .tag("outcome", "failure")
        .register(meterRegistry)

    val deleteTimer: Timer = Timer.builder("user_delete_duration")
        .description("Tempo de execução da exclusão de usuário")
        .register(meterRegistry)

    val querySuccessCounter: Counter = Counter.builder("user_query_requests_total")
        .description("Total de consultas de usuário com sucesso")
        .tag("outcome", "success")
        .register(meterRegistry)

    val queryFailureCounter: Counter = Counter.builder("user_query_requests_total")
        .description("Total de consultas de usuário com falha")
        .tag("outcome", "failure")
        .register(meterRegistry)

    val queryTimer: Timer = Timer.builder("user_query_duration")
        .description("Tempo de execução de consultas de usuário")
        .register(meterRegistry)
}
