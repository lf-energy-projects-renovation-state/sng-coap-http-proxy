// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.codec.binary.Hex
import org.eclipse.californium.core.coap.CoAP.ResponseCode
import org.eclipse.californium.core.server.resources.CoapExchange
import org.eclipse.californium.elements.util.DatagramWriter
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.CoapProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.InvalidMessageException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.logging.RemoteLogger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
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
            logger.debug { "Received CBOR: ${Hex.encodeHexString(coapExchange.requestPayload)}" }
            messageHandler.handlePost(deviceId, coapExchange.requestPayload)
                .exchangeToMono { response -> handleResponse(coapExchange, response) }
                .subscribe()
        } catch (e: Exception) {
            when (e) {
                is InvalidMessageException -> handleInvalidMessage(coapExchange)
                else -> handleUnexpectedError(coapExchange, e)
            }
        }
    }

    private fun handleResponse(coapExchange: CoapExchange, response: ClientResponse): Mono<String> {
        logger.info { "Received response with status: ${response.statusCode()}" }
        when {
            response.statusCode().is2xxSuccessful -> {
                return response.bodyToMono(String::class.java)
                    .doOnNext {
                        writeResponse(coapExchange, it!!)
                    }
            }

            response.statusCode().is4xxClientError -> {
                writeErrorResponse(coapExchange, ResponseCode.BAD_REQUEST)
            }

            else -> {
                writeErrorResponse(coapExchange, ResponseCode.INTERNAL_SERVER_ERROR)
            }
        }
        return Mono.just("error response")
    }

    private fun getIdFromRequestContext(coapExchange: CoapExchange) =
            coapExchange.advanced().currentRequest.sourceContext.peerIdentity.name

    private fun writeResponse(coapExchange: CoapExchange, body: String) {
        logger.info { "Sending successful response" }
        coapExchange.setMaxAge(1)

        coapExchange.setETag(
                DatagramWriter(4)
                        .apply { write(body.hashCode(), 32) }
                        .toByteArray()
        )

        coapExchange.respond(ResponseCode.CONTENT, body)
    }

    private fun writeErrorResponse(coapExchange: CoapExchange, responseCode: ResponseCode) {
        logger.warn { "Sending error response with status $responseCode" }
        coapExchange.setMaxAge(1)

        coapExchange.respond(responseCode)
    }

    private fun handleUnexpectedError(coapExchange: CoapExchange, e: Exception) {
        remoteLogger.error(e) { "Unexpected error occurred" }
        coapExchange.respond(ResponseCode.BAD_GATEWAY)
    }

    private fun handleInvalidMessage(coapExchange: CoapExchange) {
        coapExchange.respond(ResponseCode.BAD_GATEWAY)
    }
}
