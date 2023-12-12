plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "io.github.vinccool96.observationskt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}