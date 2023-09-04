plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "cc.suffro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object Version {
    const val JUNIT = "5.10.0"
    const val LOGGING = "2.0.11"
    const val SLF4J = "2.0.9"
    const val LOGBACK = "1.4.11"
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Version.JUNIT}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Version.JUNIT}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Version.JUNIT}")

    implementation("io.github.microutils:kotlin-logging:${Version.LOGGING}")
    implementation("org.slf4j:slf4j-simple:${Version.SLF4J}")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}