// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.http

import mu.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties.HttpProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class HttpClient(private val httpProps: HttpProperties, private val webClient: WebClient) {

    private val logger = KotlinLogging.logger { }

    fun post(message: Message): ResponseEntity<String> {
        val id = message.deviceId
        val payload = message.payload

        logger.debug { "Posting message with id $id, body: $payload" }

        return try {
            val response = post(id, payload.toString())
            logger.debug { "Posted message with id $id, resulting response: $response." }
            response
        } catch (e: Exception) {
            val error = e.message ?: "Unknown error"
            logger.error { "Failure while posting message with id $id, error: $error" }
            logger.debug { e.printStackTrace() }
            ResponseEntity("Unkown error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun post(id: String, body: String): ResponseEntity<String> {
        val response = webClient
                .post()
                .uri { uriBuilder -> uriBuilder.path(id).build() }
                .bodyValue(body)
                .header("Content-Type", "application/json")
                .retrieve()
                .toEntity(String::class.java)
                .block(httpProps.responseTimeout)

        return when {
            response == null -> ResponseEntity("No response", HttpStatus.INTERNAL_SERVER_ERROR)
            response.body.isNullOrBlank() -> ResponseEntity("Empty response", HttpStatus.INTERNAL_SERVER_ERROR)
            else -> ResponseEntity(response.body, HttpStatus.OK)
        }
    }
}
