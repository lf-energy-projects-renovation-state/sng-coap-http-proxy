// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.http

import com.fasterxml.jackson.databind.JsonNode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class HttpClient(private val webClient: WebClient) {

    companion object {
        const val ERROR_PATH = "/error"
        const val MESSAGE_PATH = "/sng"
        const val PSK_PATH = "/psk"
    }

    private val logger = KotlinLogging.logger { }

    fun postMessage(message: Message): WebClient.RequestHeadersSpec<*> {
        val (id, payload) = message

        val urc = getUrcFromMessage(payload)
        logger.debug { "Posting message with id $id, body: $payload and urc $urc" }

        try {
            val response = prepareRequest(id, payload.toString())
            logger.debug { "Posted message with id $id" }
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

    private fun prepareRequest(id: String, body: String): WebClient.RequestHeadersSpec<*> {
        return webClient
            .post()
            .uri("$MESSAGE_PATH/$id")
            .bodyValue(body)
    }
}
