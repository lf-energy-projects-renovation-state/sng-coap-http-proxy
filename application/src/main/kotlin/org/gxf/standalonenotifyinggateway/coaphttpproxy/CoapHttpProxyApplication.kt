// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy

import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties.HttpProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.CoapProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.PskStubProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.UdpProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(CoapProperties::class, UdpProperties::class, HttpProperties::class, PskStubProperties::class)
class CoapHttpProxyApplication

fun main(args: Array<String>) {
    runApplication<CoapHttpProxyApplication>(*args)
}
