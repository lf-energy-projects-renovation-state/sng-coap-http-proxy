// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "config.http")
data class HttpProperties(
        val url: String,
        val protocol: String,
        val sslBundle: String,
        val connectionTimeout: Duration,
        val responseTimeout: Duration
)
