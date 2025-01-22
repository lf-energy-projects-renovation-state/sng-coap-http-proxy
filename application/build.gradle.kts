import org.springframework.boot.gradle.tasks.bundling.BootJar

// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

plugins { id("org.springframework.boot") }

dependencies {
    implementation(libs.springBootStarterActuator)
    implementation(libs.springBootStarterWeb)
    implementation(libs.springBootStarterWebflux)

    implementation(libs.kotlinLoggingJvm)
    implementation(libs.reflect)

    implementation(libs.californiumCore)
    implementation(libs.californiumScandium)
    implementation(libs.jacksonDataformatCbor)
    implementation(libs.commonsCodec)

    runtimeOnly(libs.micrometerPrometheusModule)

    testRuntimeOnly(libs.junitPlatformLauncher)

    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.mockk)

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
                    implementation(libs.springBootStarterTest)
                    implementation(libs.californiumCore)
                    implementation(libs.californiumScandium)
                    implementation(libs.jacksonDataformatCbor)
                    implementation(libs.kotlinLoggingJvm)
                }
            }
    }
}
