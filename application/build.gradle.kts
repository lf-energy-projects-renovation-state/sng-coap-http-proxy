import org.springframework.boot.gradle.tasks.bundling.BootJar

// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

plugins { id("org.springframework.boot") }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation(libs.kotlinLoggingJvm)
    implementation(kotlin("reflect"))

    implementation(libs.californiumCore)
    implementation(libs.californiumScandium)
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")
    implementation(libs.commonsCodec)

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation(libs.mockitoKotlin)
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Generate test and integration test reports
    jacocoAggregation(project(":application"))
}

tasks.withType<BootJar> {
    // Exclude test keys and certificates
    exclude("ssl/*.pem")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
    imageName.set("ghcr.io/osgp/sng-coap-http-proxy:$version")
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
        val integrationTest by
            registering(JvmTestSuite::class) {
                useJUnitJupiter()
                dependencies {
                    implementation(project())
                    implementation(libs.wiremock)
                    implementation("org.springframework.boot:spring-boot-starter-test")
                    implementation(libs.californiumCore)
                    implementation(libs.californiumScandium)
                    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")
                    implementation(libs.kotlinLoggingJvm)
                }
            }
    }
}
