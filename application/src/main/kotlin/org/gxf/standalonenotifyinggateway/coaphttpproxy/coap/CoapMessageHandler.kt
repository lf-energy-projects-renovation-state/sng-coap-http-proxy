// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap

import mu.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.InvalidMessageException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.validation.MessageValidator
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CoapMessageHandler(private val httpClient: HttpClient) {

    private val logger = KotlinLogging.logger {}

    fun handlePost(message: Message): ResponseEntity<String> {
        logger.debug { "Handling post, for message: $message." }

        if (!MessageValidator.isValid(message)) {
            logger.warn { "Received invalid message: $message" }
            throw InvalidMessageException("Received invalid message: $message")
        }

        val response = httpClient.post(message)
        logger.debug { "Handled post, got response: $response." }
        return response
    }

}
