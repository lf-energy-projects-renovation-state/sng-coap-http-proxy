// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration

@ActiveProfiles("test")
@ContextConfiguration(initializers = [ConfigDataApplicationContextInitializer::class])
@EnableConfigurationProperties(value = [HttpProperties::class])
@ExtendWith(SpringExtension::class)
class HttpPropertiesTest {

    @Autowired
    lateinit var httpProps: HttpProperties

    @Test
    fun shouldSetConfigurationPropertiesWhenBindingPropertiesFile() {
        // arrange
        val expected = HttpProperties(
            sslEnabled = true,
            sslBundle = "sngSslBundle",
            host = "localhost",
            port = 8181,
            target = "sng-test",
            connectionTimeout = Duration.ofMillis(500),
            responseTimeout = Duration.ofMillis(500)
        )

        // act
        val actual = httpProps

        // assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }
}
