// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap

import mu.KotlinLogging
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.elements.config.Configuration
import org.eclipse.californium.scandium.DTLSConnector
import org.eclipse.californium.scandium.MdcConnectionListener
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedPskStore
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.CoapProperties
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import org.eclipse.californium.core.CoapServer as CaliforniumCoapServer

@Component
class CoapServer(
        private val config: Configuration,
        private val coapProps: CoapProperties,
        private val coapResource: CoapResource,
        private val pskStore: AdvancedPskStore
) {

    private val logger = KotlinLogging.logger { }

    private val californiumCoapServer = CaliforniumCoapServer(config)

    init {
        with(californiumCoapServer) {
            logger.info { "Starting CoAP server." }

            logger.info { "Configuring secure endpoint on port ${coapProps.coapsPort}" }

            addEndpoint(createEndpoint())
            add(coapResource)
            start()

            logger.info { "Started CoAP server." }
        }
    }

    private fun createEndpoint() =
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
