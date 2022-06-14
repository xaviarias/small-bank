package com.smallbank.infra.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jService
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider

@Component
internal class EthereumConfiguration {

    @Value("\${smallbank.ethereum.url:${HttpService.DEFAULT_URL}}")
    private var url: String? = null

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun web3j(service: Web3jService): Web3j {
        return Web3j.build(service)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun web3jService(): Web3jService {
        return HttpService(url)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun gasProvider(): ContractGasProvider {
        return DefaultGasProvider()
    }
}
