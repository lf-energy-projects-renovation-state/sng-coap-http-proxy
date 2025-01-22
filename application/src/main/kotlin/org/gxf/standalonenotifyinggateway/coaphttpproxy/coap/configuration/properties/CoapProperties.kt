// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties

import java.time.Duration
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite
import org.springframework.boot.context.properties.ConfigurationProperties

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
    val cipherSuites: List<CipherSuite>,
)
