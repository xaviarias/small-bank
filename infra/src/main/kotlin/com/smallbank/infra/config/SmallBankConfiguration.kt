package com.smallbank.infra.config

import com.smallbank.domain.model.customer.CustomerManagementService
import com.smallbank.domain.model.customer.CustomerManagementServiceImpl
import com.smallbank.domain.model.customer.CustomerRepository
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.security.SecureRandom
import java.time.Clock
import javax.validation.Validation
import javax.validation.Validator

@Configuration
@EntityScan("com.smallbank.infra.model")
@EnableJpaRepositories("com.smallbank.infra.model")
@ComponentScan(basePackages = ["com.smallbank.infra"])
internal open class SmallBankConfiguration {

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    open fun validator(): Validator = Validation.buildDefaultValidatorFactory().validator

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    open fun customerService(
        repository: CustomerRepository,
        validator: Validator
    ): CustomerManagementService {
        return CustomerManagementServiceImpl(repository, validator)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    open fun secureRandom(): SecureRandom = SecureRandom()

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    open fun systemClock(): Clock = Clock.systemUTC()
}
