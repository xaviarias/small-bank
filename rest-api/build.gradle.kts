plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    implementation(project(":smallbank-domain"))
    implementation(project(":smallbank-infra"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("com.h2database:h2:2.1.212")
}
