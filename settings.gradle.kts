// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

rootProject.name = "sng-coap-http-proxy"

include("application")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlinLogging", "7.0.0")
            version("mockitoKotlin", "5.3.1")
            version("commonsCodec", "1.17.0")
            version("californium", "3.8.0")
            version("wiremock", "3.6.0")

            library("californiumCore", "org.eclipse.californium", "californium-core").versionRef("californium")
            library("californiumScandium", "org.eclipse.californium", "scandium").versionRef("californium")

            library("kotlinLoggingJvm", "io.github.oshai", "kotlin-logging-jvm").versionRef("kotlinLogging")

            library("commonsCodec", "commons-codec", "commons-codec").versionRef("commonsCodec")

            library("mockitoKotlin", "org.mockito.kotlin", "mockito-kotlin").versionRef("mockitoKotlin")

            library("wiremock", "org.wiremock", "wiremock-standalone").versionRef("wiremock")
        }
    }
}
