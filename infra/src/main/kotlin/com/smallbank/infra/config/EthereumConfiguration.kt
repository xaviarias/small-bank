package com.smallbank.infra.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jService
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider

@Configuration
internal open class EthereumConfiguration {

    @Value("\${smallbank.ethereum.url:${HttpService.DEFAULT_URL}}")
    private var url: String? = null

    @Value("\${smallbank.ethereum.auth-token:#{null}}")
    private var authToken: String? = null

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    open fun web3j(service: Web3jService): Web3j {
        return Web3j.build(service)
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    open fun web3jService(): Web3jService {
        val clientBuilder = HttpService.getOkHttpClientBuilder()
        if (authToken != null) {
            clientBuilder.addInterceptor {
                it.proceed(
                    it.request().newBuilder() // Set authorization header
                        .header("Authorization", "Bearer $authToken")
                        .build()
                )
            }
        }
        return HttpService(url, clientBuilder.build())
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    open fun gasProvider(): ContractGasProvider {
        return DefaultGasProvider()
    }
}
