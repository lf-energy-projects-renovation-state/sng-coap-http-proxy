// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation(kotlin("reflect"))

    implementation("org.eclipse.californium:californium-core:${rootProject.extra["californiumVersion"]}")
    implementation("org.eclipse.californium:scandium:${rootProject.extra["californiumVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")

    // Test dependencies
    testImplementation("com.tngtech.archunit:archunit:${rootProject.extra["archUnitVersion"]}")
    testImplementation("com.tngtech.archunit:archunit-junit5:${rootProject.extra["archUnitVersion"]}")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
    imageName.set("ghcr.io/osgp/sng-coap-http-proxy:${version}")
    if (project.hasProperty("publishImage")) {
        publish.set(true)
        docker {
            publishRegistry {
                username.set(System.getenv("GITHUB_ACTOR"))
                password.set(System.getenv("GITHUB_TOKEN"))
            }
        }
    }
}

testing {
    suites {
        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.mock-server:mockserver-spring-test-listener:${rootProject.extra["mockServerVersion"]}")
                implementation("org.eclipse.californium:californium-core:${rootProject.extra["californiumVersion"]}")
                implementation("org.eclipse.californium:scandium:${rootProject.extra["californiumVersion"]}")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")
                implementation("io.github.microutils:kotlin-logging-jvm:${rootProject.extra["kotlinLoggingJvmVersion"]}")
                implementation(project(":components:sng-coap-http-proxy-adapter-coap-test-client"))
            }
        }
    }
}
