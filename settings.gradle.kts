// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

rootProject.name = "sng-coap-http-proxy"

include("application")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("californiumVersion", "3.8.0")
            library("californiumCore", "org.eclipse.californium", "californium-core").versionRef("californiumVersion")
            library("californiumScandium", "org.eclipse.californium", "scandium").versionRef("californiumVersion")

            library("kotlinLoggingJvm", "io.github.oshai", "kotlin-logging-jvm").version("6.0.9")

            library("commonsCodec", "commons-codec", "commons-codec").version("1.17.0")

            library("mockitoKotlin", "org.mockito.kotlin", "mockito-kotlin").version("5.3.1")

            library("wiremock", "org.wiremock", "wiremock-standalone").version("3.6.0")
        }
        create("integrationTestLibs") {
            library("h2", "com.h2database", "h2").withoutVersion()
            library("kafkaTestContainers", "org.testcontainers", "kafka").withoutVersion()
        }
    }
}
