package com.smallbank.infra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class SmallBankApplication

fun main(args: Array<String>) {
    runApplication<SmallBankApplication>(*args)
}
