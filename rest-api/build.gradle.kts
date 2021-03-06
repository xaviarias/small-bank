import java.net.URI

plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

springBoot {
    mainClass.set("com.smallbank.restapi.SmallBankApplicationKt")
}

repositories {
    maven { url = URI("https://hyperledger.jfrog.io/artifactory/besu-maven/") }
    maven { url = URI("https://artifacts.consensys.net/public/maven/maven/") }
    maven { url = URI("https://splunk.jfrog.io/splunk/ext-releases-local") }
    maven { url = URI("https://dl.cloudsmith.io/public/consensys/quorum-mainnet-launcher/maven/") }
}

dependencies {
    implementation(project(":smallbank-domain"))
    implementation(project(":smallbank-infra"))
    implementation("org.web3j:core:4.9.2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.9")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.9")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.web3j:web3j-unit:4.9.2")
    testImplementation("org.web3j:web3j-evm:4.9.2")
    runtimeOnly("com.h2database:h2:2.1.212")
}

tasks.register("bootRunTestnet") {
    group = "application"
    description = "Runs this project in Testnet"
    doFirst {
        tasks.bootRun.configure {
            systemProperty("spring.profiles.active", "testnet")
        }
    }
    finalizedBy("bootRun")
}
