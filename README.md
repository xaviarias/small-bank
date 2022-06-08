# Small Bank

API REST to simulate a small bank:

  * User registration.
  * Account creation (wallet).
  * Realization of money deposit.
  * Account display (wallet) = Balance and movements.
  * Transfer from account A to account B.

Items that are important:

  * Hexagonal architecture and testing (Required).
  * Freedom in the stack used in the test, although preferably some JVM language,
Java, Groovy or using Spring, Micronaut or Quarkus.
  * DDD Concepts.
  * Database (you can use an in-memory mock database, in-memory H2, etc).
  * Blockchain part (not required, optional). If the candidate wants to introduce part of the Blockchain, 
    he could use Truffle to generate the contracts and use them from Java and Ganache for deployment.
