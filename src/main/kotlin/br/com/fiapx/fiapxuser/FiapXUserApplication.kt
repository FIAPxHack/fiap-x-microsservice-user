package br.com.fiapx.fiapxuser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FiapXUserApplication

fun main(args: Array<String>) {
	runApplication<FiapXUserApplication>(*args)
}
