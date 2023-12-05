// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.http

import mu.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.EmptyResponseException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.configuration.properties.HttpProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.logging.RemoteLogger
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class HttpClient(private val httpProps: HttpProperties, private val webClient: WebClient, private val remoteLogger: RemoteLogger) {

    companion object {
        const val ERROR_PATH = "/error"
        const val MESSAGE_PATH = "/sng"
        const val PSK_PATH = "/psk"
    }

    private val logger = KotlinLogging.logger { }

    fun postMessage(message: Message): ResponseEntity<String> {
        val (id, payload) = message

        logger.debug { "Posting message with id $id, body: $payload" }

        try {
            val response = executeRequest(id, payload.toString())
            logger.debug { "Posted message with id $id, resulting response: $response." }
            if (response == null) {
                remoteLogger.error { "Response body for device with Id: $id is null" }
                throw EmptyResponseException("Response body for device with Id: $id is null")
            }
            return response
        } catch (e: Exception) {
            logger.error(e) { "Failure while posting message with id $id" }
            throw e
        }
    }

    private fun executeRequest(id: String, body: String): ResponseEntity<String>? {
        return webClient
                .post()
                .uri("$MESSAGE_PATH/$id")
                .bodyValue(body)
                .retrieve()
                .toEntity(String::class.java)
                .block(httpProps.responseTimeout)
    }
}
