import java.net.URI

plugins {
    id("org.web3j") version "4.9.2"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.6.0"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.6.0"
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

springBoot {
    mainClass.set("com.smallbank.infra.SmallBankApplicationKt")
}

repositories {
    maven { url = URI("https://hyperledger.jfrog.io/artifactory/besu-maven/") }
    maven { url = URI("https://artifacts.consensys.net/public/maven/maven/") }
    maven { url = URI("https://splunk.jfrog.io/splunk/ext-releases-local") }
    maven { url = URI("https://dl.cloudsmith.io/public/consensys/quorum-mainnet-launcher/maven/") }
}

web3j {
    generatedPackageName = "${group}.infra.ethereum.web3j"
}

dependencies {
    implementation(project(":smallbank-domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("com.h2database:h2:2.1.212")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-inline:4.6.1")
    testImplementation("org.web3j:web3j-unit:4.9.2")
}
