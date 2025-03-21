plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.mongodb.docs.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.0-SNAPSHOT")
    implementation("org.mongodb:mongodb-driver-core:4.10.0-SNAPSHOT")
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}