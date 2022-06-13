package com.smallbank.infra

import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ComponentScan
@EnableJpaRepositories(
    basePackages = ["com.smallbank.infra.model"]
)
internal open class SmallBankConfiguration
