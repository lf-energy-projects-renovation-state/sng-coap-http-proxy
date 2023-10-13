// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.coap.server

import mu.KotlinLogging
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.elements.config.Configuration
import org.eclipse.californium.scandium.DTLSConnector
import org.eclipse.californium.scandium.MdcConnectionListener
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedPskStore
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.coap.server.configuration.properties.CoapProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import org.eclipse.californium.core.CoapServer as CaliforniumCoapServer

@Component
class CoapServer(
    @Qualifier("serverConfiguration") val config: Configuration,
    val coapProps: CoapProperties,
    val coapResource: CoapResource,
    val pskStore: AdvancedPskStore
) {

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }

    private val californiumCoapServer = CaliforniumCoapServer(config)

    init {
        with(californiumCoapServer) {
            LOGGER.info { "Starting CoAP server." }

            LOGGER.info { "Configuring secure endpoint on port ${coapProps.coapsPort}" }
            addEndpoint(createSecureEndpoint())

            add(coapResource)
            start()

            LOGGER.info { "Started CoAP server." }
        }
    }

    fun shutDown() {
        with(californiumCoapServer) {
            stop()
            destroy()
        }
    }

    private fun sanitizePath(path: String) = if (path.startsWith("/")) path.substring(1) else path

    private fun createSecureEndpoint() =
        CoapEndpoint.Builder()
            .setConfiguration(config)
            .setConnector(createDtlsConnector())
            .build()

    private fun createDtlsConnector() = DTLSConnector(
        DtlsConnectorConfig
            .builder(config)
            .setAddress(InetSocketAddress(coapProps.coapsPort))
            .setAdvancedPskStore(pskStore)
            .setConnectionListener(MdcConnectionListener())
            .build()
    )
}
