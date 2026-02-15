package br.com.fiapx.fiapxuser.bdd.config

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.web.server.LocalServerPort

@CucumberContextConfiguration
class CucumberSpringConfiguration : AbstractIntegrationTest() {

    @LocalServerPort
    protected var port: Int = 0

    protected fun getBaseUrl(): String = "http://localhost:$port"
}

