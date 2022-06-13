package com.smallbank.infra

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@ComponentScan
@EntityScan("com.smallbank.infra.model")
@EnableJpaRepositories("com.smallbank.infra.model")
internal open class SmallBankConfiguration
