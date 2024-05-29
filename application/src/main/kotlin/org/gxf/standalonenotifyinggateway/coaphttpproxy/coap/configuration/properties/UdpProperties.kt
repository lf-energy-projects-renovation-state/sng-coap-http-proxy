// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties

import java.time.Duration
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "config.udp")
class UdpProperties(
    val udpReceiveBufferSize: Int,
    val udpSendBufferSize: Int,
    val healthStatusIntervalInSeconds: Duration,
)
