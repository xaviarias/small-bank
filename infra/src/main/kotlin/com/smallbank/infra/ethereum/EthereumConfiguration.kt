package com.smallbank.infra.ethereum

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

    private lateinit var web3j: Web3j
    private lateinit var gasProvider: ContractGasProvider

    @Value("\${smallbank.ethereum.url:#{null}}")
    private var url: String? = HttpService.DEFAULT_URL

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun web3j(service: Web3jService): Web3j {
        web3j = Web3j.build(service)
        return web3j
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun web3jService(): Web3jService {
        return HttpService(url)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    fun gasProvider(): ContractGasProvider {
        gasProvider = DefaultGasProvider()
        return gasProvider
    }
}
