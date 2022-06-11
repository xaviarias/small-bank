package com.smallbank.infra.model.account

import com.smallbank.domain.model.account.Account
import com.smallbank.domain.model.account.AccountId
import com.smallbank.domain.model.account.AccountManagementService
import com.smallbank.domain.model.account.AccountMovement
import com.smallbank.domain.model.customer.CustomerId
import com.smallbank.infra.ethereum.EthereumKeyVault
import com.smallbank.infra.ethereum.web3j.SmallBank
import org.springframework.stereotype.Service
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.gas.ContractGasProvider
import java.math.BigDecimal

@Service
internal class EthereumAccountManagementService(
    private val web3j: Web3j,
    private val gasProvider: ContractGasProvider,
    private val keyRepository: EthereumKeyVault,
    private val contractAddress: String
) : AccountManagementService {

    override fun create(customerId: CustomerId): Account {
        // There's no impact on the Ethereum side
        return Account(AccountId.create(), customerId, BigDecimal.ZERO)
    }

    override fun deposit(customerId: CustomerId, amount: BigDecimal) {
        contract(customerId).deposit(amount.toBigInteger()).send()
    }

    override fun withdraw(customerId: CustomerId, amount: BigDecimal) {
        contract(customerId).withdraw(amount.toBigInteger()).send()
    }

    override fun balance(customerId: CustomerId): BigDecimal {
        return contract(customerId).balance().send().toBigDecimal()
    }

    // TODO Support date intervals
    override fun movements(customerId: CustomerId): List<AccountMovement> {
//        val deposits = contract(customerId).accountDepositEventFlowable(
//            DefaultBlockParameterName.EARLIEST,
//            DefaultBlockParameterName.LATEST
//        ).blockingIterable().map {
//            AccountMovement(MovementType.DEPOSIT, it.amount.toBigDecimal())
//        }
        TODO()
    }

    private fun contract(customerId: CustomerId): SmallBank {
        val privateKey = keyRepository.resolve(customerId)
        return SmallBank.load(contractAddress, web3j, Credentials.create(privateKey), gasProvider)
    }
}
