// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.coap.server.configuration.properties

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
@EnableConfigurationProperties(value = [UdpProperties::class])
@ExtendWith(SpringExtension::class)
class UdpPropertiesTest(@Autowired val udpProperties: UdpProperties) {

    @Test
    fun shouldSetConfigurationPropertiesWhenBindingPropertiesFile() {
        // arrange
        val expected = UdpProperties(
            udpReceiveBufferSize = 8193,
            udpSendBufferSize = 8194,
            healthStatusInterval = Duration.ofSeconds(61)
        )

        // act
        val actual = udpProperties

        // assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

}
