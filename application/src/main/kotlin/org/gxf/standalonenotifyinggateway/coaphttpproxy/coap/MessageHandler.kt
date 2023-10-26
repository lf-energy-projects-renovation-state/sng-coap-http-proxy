// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import mu.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.InvalidMessageException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.validation.MessageValidator
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.ProxyError
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class MessageHandler(private val httpClient: HttpClient, private val messageValidator: MessageValidator) {

    private val logger = KotlinLogging.logger {}
    private val cborMapper = CBORMapper()

    fun handlePost(id: String, payload: ByteArray): ResponseEntity<String> {
        val parsedJson = cborMapper.readTree(payload)
        val message = Message(id, parsedJson)

        logger.trace { "Handling post, for message: $message." }

        if (messageValidator.isValid(message)) {
            val response = httpClient.postMessage(message)
            logger.debug { "Handled post, got response: $response." }
            return response
        }

        throw InvalidMessageException("Received invalid message: $message")
    }

    fun handleErrorPost(error: ProxyError) {
        httpClient.postError(error)
    }
}
