// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import mu.KotlinLogging
import org.eclipse.californium.core.coap.CoAP.ResponseCode
import org.eclipse.californium.core.server.resources.CoapExchange
import org.eclipse.californium.elements.util.DatagramWriter
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.CoapProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.springframework.stereotype.Component
import org.eclipse.californium.core.CoapResource as CaliforniumCoapResource

@Component
class CoapResource(private val coapProps: CoapProperties, private val coapMessageHandler: CoapMessageHandler) :
        CaliforniumCoapResource(coapProps.path) {

    private val logger = KotlinLogging.logger { }
    private val cborMapper = CBORMapper()

    init {
        logger.info { "Initializing Coap resource for path: ${coapProps.path}" }
    }

    override fun handlePOST(coapExchange: CoapExchange) {
        logger.debug { "Handling CoAP POST: $coapExchange" }

        try {
            val deviceId = getIdFromRequestContext(coapExchange)
            logger.debug("Device ID from request context: $deviceId")
            val parsedJson = cborMapper.readTree(coapExchange.requestPayload)
            val response = coapMessageHandler.handlePost(Message(deviceId, parsedJson))
            // Intentional exception throwing when the response is null or when there is no body
            writeResponse(coapExchange, response!!.body!!)
        } catch (e: Exception) {
            handleFailure(coapExchange, e)
        }
    }

    private fun getIdFromRequestContext(coapExchange: CoapExchange) =
            coapExchange.advanced().currentRequest.sourceContext.peerIdentity.name

    private fun writeResponse(coapExchange: CoapExchange, body: String) {
        coapExchange.setMaxAge(1)

        coapExchange.setETag(
                DatagramWriter(4)
                        .apply { write(body.hashCode(), 32) }
                        .toByteArray()
        )

        coapExchange.respond(ResponseCode.CONTENT, body)
    }

    private fun handleFailure(coapExchange: CoapExchange, e: Exception) {
        logger.error { "Error while processing message from device: $e" }
        logger.debug { e.printStackTrace() }
        coapExchange.respond(ResponseCode.BAD_GATEWAY);
    }
}
