import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer

plugins {
    kotlin("jvm") version "1.3.70"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    application
}

group = "nl.recognize.csv2xlsx"
version = "0.1.0"

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/ktor")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("org.apache.poi:poi-ooxml:4.1.2")
    implementation("io.ktor:ktor-server-core:1.3.2")
    implementation("io.ktor:ktor-server-netty:1.3.2")
    implementation("com.opencsv:opencsv:5.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<ShadowJar> {
        transform(ServiceFileTransformer::class.java) {
            setPath("META-INF/services")
            include("org.eclipse.jetty.http.HttpFieldPreEncoder")
        }

        archiveFileName.set("app.jar")
    }
}

application {
    mainClassName = "nl.recognize.csv2xlsx.MainKt"
}

