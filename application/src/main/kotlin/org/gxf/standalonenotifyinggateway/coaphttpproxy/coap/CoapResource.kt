// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap

import mu.KotlinLogging
import org.eclipse.californium.core.coap.CoAP.ResponseCode
import org.eclipse.californium.core.server.resources.CoapExchange
import org.eclipse.californium.elements.util.DatagramWriter
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.CoapProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.EmptyResponseException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.InvalidMessageException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.logging.RemoteLogger
import org.springframework.stereotype.Component
import org.eclipse.californium.core.CoapResource as CaliforniumCoapResource

@Component
class CoapResource(private val coapProps: CoapProperties, private val messageHandler: MessageHandler, private val remoteLogger: RemoteLogger) :
        CaliforniumCoapResource(coapProps.path) {

    private val logger = KotlinLogging.logger { }

    init {
        logger.info { "Initializing Coap resource for path: ${coapProps.path}" }
    }

    override fun handlePOST(coapExchange: CoapExchange) {
        logger.debug { "Handling CoAP POST: $coapExchange" }

        try {
            val deviceId = getIdFromRequestContext(coapExchange)
            logger.debug { "Device ID from request context: $deviceId" }
            val response = messageHandler.handlePost(deviceId, coapExchange.requestPayload)
            // Intentional exception throwing when the response is null or when there is no body
            writeResponse(coapExchange, response.body!!)
        } catch (e: Exception) {
            when (e) {
                is EmptyResponseException -> handleHttpFailure(coapExchange)
                is InvalidMessageException -> handleInvalidMessage(coapExchange)
                else -> handleUnexpectedError(coapExchange, e)
            }
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

    private fun handleUnexpectedError(coapExchange: CoapExchange, e: Exception) {
        remoteLogger.error(e) { "Unexpected error occurred" }
        coapExchange.respond(ResponseCode.BAD_GATEWAY)
    }

    private fun handleInvalidMessage(coapExchange: CoapExchange) {
        coapExchange.respond(ResponseCode.BAD_GATEWAY)
    }

    private fun handleHttpFailure(coapExchange: CoapExchange) {
        writeResponse(coapExchange, "0")
    }
}
