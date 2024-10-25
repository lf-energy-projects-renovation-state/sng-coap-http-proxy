// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

rootProject.name = "sng-coap-http-proxy"

include("application")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlinLogging", "7.0.0")
            version("mockk", "1.13.13")
            version("commonsCodec", "1.17.1")
            version("californium", "3.8.0")
            version("wiremock", "3.6.0")

            library("californiumCore", "org.eclipse.californium", "californium-core").versionRef("californium")
            library("californiumScandium", "org.eclipse.californium", "scandium").versionRef("californium")

            library("kotlinLoggingJvm", "io.github.oshai", "kotlin-logging-jvm").versionRef("kotlinLogging")

            library("commonsCodec", "commons-codec", "commons-codec").versionRef("commonsCodec")

            library("mockk", "io.mockk", "mockk").versionRef("mockk")

            library("wiremock", "org.wiremock", "wiremock-standalone").versionRef("wiremock")
        }
    }
}
