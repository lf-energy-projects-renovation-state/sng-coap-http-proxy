// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.architecture.rules

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import org.springframework.context.annotation.Configuration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication

object SpringConfigurationRules {

    fun isSpringContextClass() = object : DescribedPredicate<JavaClass>("Spring configuration class") {
      override fun test(input: JavaClass) =
        (input.isAnnotatedWith(Configuration::class.java)
          .and(input.getSimpleName().endsWith("Context")))
    }

    fun isSpringApplicationClass() = object : DescribedPredicate<JavaClass>("Spring application class") {
      override fun test(input: JavaClass) = input.isAnnotatedWith(SpringBootApplication::class.java)
        // Kotlin generates ...Kt files for files containing methods outside the class, like the static main method
        .or(input.getSimpleName().endsWith("ApplicationKt"))
    }

    fun isSpringConfigurationClass() = object : DescribedPredicate<JavaClass>("Spring configuration class") {
      override fun test(input: JavaClass) = input.isAnnotatedWith(Configuration::class.java)
    }

    fun isSpringConfigurationPropertiesClass() =
      object : DescribedPredicate<JavaClass>("Spring configuration properties class") {
        override fun test(input: JavaClass) = input.isAnnotatedWith(ConfigurationProperties::class.java)
      }

    fun isSpringConfigurationPropertiesTestClass() =
      object : DescribedPredicate<JavaClass>("Spring configuration properties class") {
        override fun test(input: JavaClass) = input.getSimpleName().endsWith("ConfigurationPropertiesTest")
      }
}
