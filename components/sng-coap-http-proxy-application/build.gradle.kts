// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:${rootProject.extra["kotlinLoggingJvmVersion"]}")
    implementation("ch.qos.logback:logback-classic")

    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Stand-alone notifying gateway
    api(project(":components:sng-coap-http-proxy-domain"))

    // Test dependencies
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${rootProject.extra["mockitoKotlinVersion"]}")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")

}
