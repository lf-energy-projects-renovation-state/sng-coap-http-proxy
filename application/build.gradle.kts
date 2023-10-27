// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("org.springframework.boot")
}

dependencies {
    jacocoAggregation(project(":application"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation(kotlin("reflect"))

    implementation("org.eclipse.californium:californium-core:${rootProject.extra["californiumVersion"]}")
    implementation("org.eclipse.californium:scandium:${rootProject.extra["californiumVersion"]}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
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
                implementation("org.wiremock:wiremock:3.2.0")
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.mock-server:mockserver-spring-test-listener:${rootProject.extra["mockServerVersion"]}")
                implementation("org.eclipse.californium:californium-core:${rootProject.extra["californiumVersion"]}")
                implementation("org.eclipse.californium:scandium:${rootProject.extra["californiumVersion"]}")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor")
                implementation("io.github.microutils:kotlin-logging-jvm:${rootProject.extra["kotlinLoggingJvmVersion"]}")
            }
        }
    }
}


// Integrate INTEGRATION_TEST results into the aggregated UNIT_TEST coverage results
tasks.testCodeCoverageReport {
    sourceSets(sourceSets.main.get())
    executionData.from(
            configurations.aggregateCodeCoverageReportResults.get()
                    .incoming.artifactView {
                        lenient(true)
                        withVariantReselection()
                        attributes {
                            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.VERIFICATION))
                            attribute(TestSuiteType.TEST_SUITE_TYPE_ATTRIBUTE, objects.named(TestSuiteType.INTEGRATION_TEST))
                            attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, objects.named(VerificationType.JACOCO_RESULTS))
                            attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.BINARY_DATA_TYPE)
                        }
                    }.files
    )

    finalizedBy("copyReport")
}

tasks.register<Copy>("copyReport") {
    from(layout.buildDirectory.file("reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml"))
    into(layout.buildDirectory.dir("jacoco/"))
    rename("testCodeCoverageReport.xml", "jacoco.xml")
}

tasks.check {
    dependsOn(tasks.testAggregateTestReport)
    dependsOn(tasks.testCodeCoverageReport)
}
