package com.smallbank.infra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@Configuration
@SpringBootApplication
open class SmallBankApplication

fun main(args: Array<String>) {
    runApplication<SmallBankApplication>(*args)
}
