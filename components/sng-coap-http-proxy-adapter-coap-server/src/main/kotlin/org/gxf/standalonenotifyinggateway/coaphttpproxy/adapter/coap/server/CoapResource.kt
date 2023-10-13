// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.coap.server

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import mu.KotlinLogging
import org.eclipse.californium.core.coap.CoAP.ResponseCode
import org.eclipse.californium.core.server.resources.CoapExchange
import org.eclipse.californium.elements.util.DatagramWriter
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.coap.server.configuration.properties.CoapProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.application.ports.input.CoapMessageInputPort
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Payload
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.ErrorResponse
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.OkResponse
import org.springframework.stereotype.Component
import org.eclipse.californium.core.CoapResource as CaliforniumCoapResource

@Component
class CoapResource(val coapProps: CoapProperties, val coapMessageInputPort: CoapMessageInputPort) :
    CaliforniumCoapResource(coapProps.path) {

    companion object {
        private val logger = KotlinLogging.logger { }

        private val cborMapper = CBORMapper()
    }

    init {
        logger.info { "Initializing Coap resource for path: ${coapProps.path}" }
    }

    private val cborMapper = CBORMapper()
    private val jsonMapper = JsonMapper()

    override fun handlePOST(coapExchange: CoapExchange) {
        logger.debug("Handling CoAP POST: $coapExchange")

        try {
            val deviceId = getIdFromRequestContext(coapExchange)
            logger.debug("Device ID from request context: $deviceId")
            val cborBytes = coapExchange.getRequestPayload()
            val jsonNode = cborMapper.readTree(cborBytes)

            val response = coapMessageInputPort.handlePost(Message(deviceId, Payload(jsonNode)))

            when (response) {
                is OkResponse -> writeResponse(coapExchange, response)
                is ErrorResponse -> handleFailure(coapExchange, response)
            }
        } catch (e: Exception) {
            handleFailure(coapExchange, e)
        }
    }

    private fun getIdFromRequestContext(coapExchange: CoapExchange) =
        coapExchange.advanced().getCurrentRequest().getSourceContext().getPeerIdentity().getName()

    private fun writeResponse(coapExchange: CoapExchange, response: OkResponse) {
        coapExchange.setMaxAge(1)

        coapExchange.setETag(
            DatagramWriter(4)
                .apply { write(response.hashCode(), 32) }
                .toByteArray()
        )

        coapExchange.respond(ResponseCode.CONTENT, response.body)
    }

    private fun handleFailure(coapExchange: CoapExchange, response: ErrorResponse) {
        logger.error { "Error response while processing message from device: ${response.error}." }
        coapExchange.respond(ResponseCode.BAD_GATEWAY);
    }

    private fun handleFailure(coapExchange: CoapExchange, e: Exception) {
        logger.error { "Error while processing message from device: $e" }
        logger.debug { e.printStackTrace() }
        coapExchange.respond(ResponseCode.BAD_GATEWAY);
    }
}