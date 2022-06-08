package com.smallbank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class SmallBankApiApplication

fun main(args: Array<String>) {
	runApplication<SmallBankApiApplication>(*args)
}
