// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

import com.diffplug.gradle.spotless.SpotlessExtension
import io.spring.gradle.dependencymanagement.internal.dsl.StandardDependencyManagementExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.dependencyManagement) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.spring) apply false
    alias(libs.plugins.jpa) apply false
    alias(libs.plugins.avro) apply false
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.spotless)
    alias(libs.plugins.eclipse)
}

version = System.getenv("GITHUB_REF_NAME")?.replace("/", "-")?.lowercase() ?: "develop"

sonar {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.projectKey", "OSGP_sng-coap-http-proxy")
        property("sonar.organization", "gxf")
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.get().pluginId)
    apply(plugin = rootProject.libs.plugins.spring.get().pluginId)
    apply(plugin = rootProject.libs.plugins.dependencyManagement.get().pluginId)
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
    apply(plugin = rootProject.libs.plugins.eclipse.get().pluginId)
    apply(plugin = rootProject.libs.plugins.jpa.get().pluginId)
    apply(plugin = rootProject.libs.plugins.jacoco.get().pluginId)
    apply(plugin = rootProject.libs.plugins.jacocoReportAggregation.get().pluginId)

    group = "org.gxf.standalonenotifyinggateway"
    version = rootProject.version

    repositories { mavenCentral() }

    extensions.configure<SpotlessExtension> {
        kotlin {
            ktfmt().kotlinlangStyle().configure {
                it.setMaxWidth(120)
            }

            licenseHeaderFile("${project.rootDir}/license-template.kt", "package").updateYearWithLatest(false)
        }
        kotlinGradle {
            ktfmt().kotlinlangStyle().configure {
                it.setMaxWidth(120)
            }
        }
    }

    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
        compilerOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    extensions.configure<StandardDependencyManagementExtension> {
        imports { mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES) }
    }

    tasks.withType<Test> { useJUnitPlatform() }

    tasks.register<Copy>("updateGitHooks") {
        description = "Copies the pre-commit Git Hook to the .git/hooks folder."
        group = "verification"
        from("${project.rootDir}/scripts/pre-commit")
        into("${project.rootDir}/.git/hooks")
    }

    tasks.withType<KotlinCompile> {
        dependsOn(
            tasks.named("updateGitHooks")
        )
    }
}
