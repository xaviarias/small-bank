# SmallBank

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

## Dependencies

* Java 11
* Web3j
* Spring Boot
* Docker (to run tests)

## Design and architecture

The project is split between three Gradle submodules, which correspond to the hexagonal architecture concepts
of Domain Model, Ports and Application Services, and Adapters.

For simplicity, I have defined in the same domain the customer and account-related elements,
although conceptually they could be considered as different subdomains and bounded contexts.
This is reflected in the package structure, that follows the DDD concept of modules.

### Domain

Corresponds to the DDD concept of ubiquitous language. Contains domain objects and application services,
performs validation logic and defines transaction boundaries.

This would correspond to the Domain Model a the hexagonal architecture, with no runtime dependencies.

### Infrastructure

Defines the bounded context in terms of DDD, here simplified as a single one. 
As mentioned, it could be split into several bounded contexts, one for customer-related elements,
and the other for accounts.

Implements the JPA and Ethereum adapters for the SmallBank Domain, relying on JPA to persist customer-related data,
and on an Ethereum smart contract for accounts-specific data, such as balances and movements.

This would correspond to the outer hexagon, with runtime dependencies such as JPA and Spring.

### Web Application

The web application implements a REST API which invokes the SmallBank services defined in the domain module.

## Building and testing

To build the project and run the tests, run the commands:

```
./gradlew generateContractWrappers 
./gradlew build 
```

The project requires a Docker environment to run the tests.

### Configuration

The REST API application requires some configuration to run:

* `smallbank.ethereum.url`: The Ethereum endpoint (defaults to `http://localhost:8545/`).
* `smallbank.ethereum.account`: The admin account to deploy the initial contract (mandatory).
* `smallbank.ethereum.private-key`: The credentials for the admin account (for tests only).
* `smallbank.ethereum.contract.address`: If you want to use an already deployed contract (optional).

### Run the REST API

Run the commands:

```
cd rest-api
./gradlew bootRun 
```
