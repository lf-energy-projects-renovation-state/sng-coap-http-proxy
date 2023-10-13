// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Kotlin reflect is needed for constructor binding of configuration properties using immutable data classes
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.github.microutils:kotlin-logging-jvm:${rootProject.extra["kotlinLoggingJvmVersion"]}")
    implementation("ch.qos.logback:logback-classic")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Stand-alone notifying gateway
    api(project(":components:sng-coap-http-proxy-domain"))
    api(project(":components:sng-coap-http-proxy-application"))

    // Test dependencies
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("org.mock-server:mockserver-spring-test-listener:${rootProject.extra["mockServerVersion"]}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${rootProject.extra["mockitoKotlinVersion"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
