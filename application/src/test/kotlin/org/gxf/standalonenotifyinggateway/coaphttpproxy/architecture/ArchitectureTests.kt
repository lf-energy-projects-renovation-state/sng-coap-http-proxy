// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.base.DescribedPredicate.alwaysTrue
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.definitions.PackageDefinitions.ADAPTER_PACKAGE
import org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.definitions.PackageDefinitions.APPLICATION_PACKAGE
import org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.definitions.PackageDefinitions.ARCHITECTURE_TESTS_PACKAGE
import org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.definitions.PackageDefinitions.BASE_PACKAGE
import org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.definitions.PackageDefinitions.DOMAIN_MODEL_PACKAGE
import org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.definitions.PackageDefinitions.DOMAIN_SERVICE_PACKAGE
import org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.rules.SpringConfigurationRules.isSpringApplicationClass
import org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.rules.SpringConfigurationRules.isSpringContextClass

@AnalyzeClasses(packages = [BASE_PACKAGE])
class ArchitectureTest {

    @ArchTest
    val `Onion architecture should be respected` = onionArchitecture()
        .domainModels("$DOMAIN_MODEL_PACKAGE..")
        .domainServices("$DOMAIN_SERVICE_PACKAGE..")
        .applicationServices("$APPLICATION_PACKAGE..")
        .adapter("coap", "$ADAPTER_PACKAGE.coap.server..")
        .adapter("http", "$ADAPTER_PACKAGE.http.client..")
        .adapter("psk", "$ADAPTER_PACKAGE.psk..")

        .ignoreDependency(
            isSpringContextClass(),
            alwaysTrue()
        )

        .ensureAllClassesAreContainedInArchitectureIgnoring(
            isSpringApplicationClass()
                .or(isSpringContextClass())
                .or(isArchitectureTestClass())
        )
        .withOptionalLayers(true)

    fun isArchitectureTestClass() = object : DescribedPredicate<JavaClass>("Architecture test class") {
        override fun test(input: JavaClass) =
            input.getPackageName().startsWith("$ARCHITECTURE_TESTS_PACKAGE")
    }

}
