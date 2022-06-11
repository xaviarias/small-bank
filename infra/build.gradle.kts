import java.net.URI

plugins {
    id("org.web3j") version "4.9.2"
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
    project(":smallbank-domain")
    testImplementation("org.web3j:web3j-unit:4.9.2")
}
