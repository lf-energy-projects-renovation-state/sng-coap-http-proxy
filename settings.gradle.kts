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

            library("kotlinLoggingJvm", "io.github.oshai", "kotlin-logging-jvm").version("6.0.1")

            library("mockServer", "org.mock-server", "mockserver-spring-test-listener").version("5.15.0")

            library("commonsCodec", "commons-codec", "commons-codec").version("1.16.0")

            library("mockitoKotlin", "org.mockito.kotlin", "mockito-kotlin").version("5.1.0")

            library("wiremock", "org.wiremock", "wiremock-standalone").version("3.3.1")
        }
        create("integrationTestLibs") {
            library("h2", "com.h2database", "h2").version("2.2.224")
            library("kafkaTestContainers", "org.testcontainers", "kafka").version("1.19.7")
        }
    }
}
