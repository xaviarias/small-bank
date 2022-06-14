package com.smallbank.restapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Order(Ordered.HIGHEST_PRECEDENCE)
@SpringBootApplication(scanBasePackages = ["com.smallbank"])
open class SmallBankApplication : SpringBootServletInitializer()

fun main(args: Array<String>) {
    runApplication<SmallBankApplication>(*args)
}
