plugins {
    kotlin("jvm") version "1.6.21"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        configurations.all {
            resolutionStrategy {
                force("com.fasterxml.jackson.core:jackson-core:2.13.3")
                force("org.junit.jupiter:junit-jupiter-api:5.8.2")
                force("org.apache.logging.log4j:log4j-core:2.17.2")
                force("org.apache.logging.log4j:log4j-jul:2.17.2")
                force("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")
                force("ch.qos.logback:logback-classic:1.2.11")
                force("org.slf4j:slf4j-api:1.7.36")
                force("org.ow2.asm:asm:9.2")
                force("org.ow2.asm:asm-analisys:9.2")
                force("org.ow2.asm:asm-commons:9.2")
                force("org.ow2.asm:asm-util:9.2")
                force("com.beust:klaxon:5.6")
            }
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
    tasks.test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
    }
}
