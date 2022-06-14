package com.smallbank.restapi

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.web3j.NodeType
import org.web3j.container.ServiceBuilder
import org.web3j.protocol.Web3jService

@Configuration
open class SmallBankApplicationTestConfiguration {

    @Value("\${smallbank.ethereum.account}")
    private var ethereumAccount: String? = null

    @Bean
    @Primary
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    open fun web3jService(): Web3jService {
        return ServiceBuilder()
            .type(NodeType.BESU)
            .withGenesis("dev")
            .withSelfAddress(ethereumAccount!!)
            .build()
            .startService()
    }

    @Bean
    open fun restTemplateBuilder(): RestTemplateBuilder {
        return TimeoutTemplateBuilder()
    }

    class TimeoutTemplateBuilder : RestTemplateBuilder() {
        override fun buildRequestFactory(): ClientHttpRequestFactory {
            return (super.buildRequestFactory() as OkHttp3ClientHttpRequestFactory).apply {
                setConnectTimeout(1200000);
                setReadTimeout(1200000);
            }
        }
    }
}
