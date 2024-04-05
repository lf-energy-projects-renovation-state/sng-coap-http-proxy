// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "config.coap")
class CoapProperties(
    val coapsPort: Int,
    val path: String,
    val deduplicator: String,
    val maxActivePeers: Int,
    val maxMessageSize: Int,
    val maxPeerInactivityPeriod: Duration,
    val maxResourceBodySize: Int,
    val preferredBlockSize: Int,
)
