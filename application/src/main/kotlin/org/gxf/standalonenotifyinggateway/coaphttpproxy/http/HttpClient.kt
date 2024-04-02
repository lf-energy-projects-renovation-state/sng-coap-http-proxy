// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.http

import com.fasterxml.jackson.databind.JsonNode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.EmptyResponseException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.configuration.properties.HttpProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.exception.BadRequestException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.exception.InternalServerErrorException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.logging.RemoteLogger
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity

@Component
class HttpClient(
    private val httpProps: HttpProperties,
    private val webClient: WebClient,
    private val remoteLogger: RemoteLogger
) {

    companion object {
        const val ERROR_PATH = "/error"
        const val MESSAGE_PATH = "/sng"
        const val PSK_PATH = "/psk"
    }

    private val logger = KotlinLogging.logger { }

    @Throws(
        BadRequestException::class,
        InternalServerErrorException::class,
        EmptyResponseException::class
    )
    fun postMessage(message: Message): ResponseEntity<String>? {
        val (id, payload) = message

        val urc = getUrcFromMessage(payload)
        logger.debug { "Posting message with id $id, body: $payload and urc $urc" }

        try {
            val response = executeRequest(id, payload.toString())
            logger.debug { "Posted message with id $id, resulting reponse: $response" }
            if (response == null) {
                remoteLogger.error { "Response body for device with Id: $id is null" }
                throw EmptyResponseException("Response body for device with Id: $id is null")
            }
            return response
        } catch (e: Exception) {
            logger.error(e) { "Failure while posting message with id $id and $urc" }
            throw e
        }
    }

    private fun getUrcFromMessage(body: JsonNode) = body["URC"]
        .filter { it.isTextual }
        .map { it.asText() }
        .firstOrNull()

    @Throws(BadRequestException::class, InternalServerErrorException::class)
    private fun executeRequest(id: String, body: String): ResponseEntity<String>? = webClient
            .post()
            .uri("$MESSAGE_PATH/$id")
            .bodyValue(body)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) { response ->
            response.bodyToMono(String::class.java)
                .map { BadRequestException("Client error response received") }
        }
        .onStatus(HttpStatusCode::is5xxServerError) { response ->
            response.bodyToMono(String::class.java)
                .map { InternalServerErrorException("Server error response received") }
        }
        .toEntity<String>()
        .block(httpProps.responseTimeout)
}
