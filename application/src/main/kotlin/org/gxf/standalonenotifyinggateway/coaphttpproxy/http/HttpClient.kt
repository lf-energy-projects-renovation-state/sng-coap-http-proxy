// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.http

import mu.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.ProxyError
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.configuration.properties.HttpProperties
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class HttpClient(private val httpProps: HttpProperties, private val webClient: WebClient) {

    companion object {
        const val ERROR_PATH = "/error"
        const val MESSAGE_PATH = "/sng"
    }

    private val logger = KotlinLogging.logger { }

    fun postError(proxyError: ProxyError) {
        executeErrorRequest(proxyError)
    }

    fun postMessage(message: Message): ResponseEntity<String>? {
        val (id, payload) = message

        logger.debug { "Posting message with id $id, body: $payload" }

        try {
            val response = executeRequest(id, payload.toString())
            logger.debug { "Posted message with id $id, resulting response: $response." }
            return response
        } catch (e: Exception) {
            val error = e.message ?: "Unknown error"
            logger.error { "Failure while posting message with id $id, error: $error" }
            logger.debug { e.printStackTrace() }
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

    private fun executeErrorRequest(body: ProxyError) {
        webClient
                .post()
                .uri(ERROR_PATH)
                .bodyValue(body)
                .retrieve()
                .bodyToMono<Unit>()
                .doOnError {
                    logger.error { it }
                }
                .block(httpProps.responseTimeout)
    }
}
