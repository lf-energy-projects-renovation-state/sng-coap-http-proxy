// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    // Kotlin reflect is needed for constructor binding of configuration properties using immutable data classes
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("io.github.microutils:kotlin-logging-jvm:${rootProject.extra["kotlinLoggingJvmVersion"]}")
    implementation("ch.qos.logback:logback-classic")

    // Californium CoAP framework}
    implementation("org.eclipse.californium:californium-core:${rootProject.extra["californiumVersion"]}")
    implementation("org.eclipse.californium:scandium:${rootProject.extra["californiumVersion"]}")

    // CBOR
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Stand-alone notifying gateway
    api(project(":components:sng-coap-http-proxy-domain"))
    api(project(":components:sng-coap-http-proxy-application"))

    // Test dependencies
    testImplementation("org.mockito.kotlin:mockito-kotlin:${rootProject.extra["mockitoKotlinVersion"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

