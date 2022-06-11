package com.smallbank.infra.model.customer

import com.smallbank.domain.model.customer.CustomerRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaCustomerRepository : CustomerRepository {
}
