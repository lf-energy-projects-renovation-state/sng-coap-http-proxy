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
@EnableConfigurationProperties(value = [CoapProperties::class])
@ExtendWith(SpringExtension::class)
class CoapPropertiesTest() {

    @Autowired
    lateinit var configProps: CoapProperties

    @Test
    fun shouldSetConfigurationPropertiesWhenBindingPropertiesFile() {
        // arrange
        val expected = CoapProperties(
            coapsPort = 55684,
            path = "sng-test",
            deduplicator = "NO_DEDUPLICATOR",
            maxActivePeers = 20001,
            maxMessageSize = 1025,
            maxPeerInactivityPeriod = Duration.ofHours(25),
            maxResourceBodySize = 8193,
            preferredBlockSize = 1026
        )

        // act
        val actual = configProps

        // assert
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

}
