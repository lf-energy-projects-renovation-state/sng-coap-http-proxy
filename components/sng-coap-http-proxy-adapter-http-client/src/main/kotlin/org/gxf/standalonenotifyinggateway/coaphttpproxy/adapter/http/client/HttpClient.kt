// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client

import mu.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties.HttpProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.application.ports.output.HttpMessageOutputPort
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.ErrorResponse
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.OkResponse
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class HttpClient(private val httpProps: HttpProperties, private val webClient: WebClient) : HttpMessageOutputPort {

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }

    override fun post(message: Message): Response {
        val id = message.deviceId
        val payload = message.payload.jsonString

        LOGGER.debug { "Posting message with id $id, body: $payload" }

        try {
            val response = post(id, payload)
            LOGGER.debug { "Posted message with id $id, resulting response: $response." }
            return response
        } catch (e: Exception) {
            val error = e.message ?: "Unknown error"
            LOGGER.error { "Failure while posting message with id $id, error: $error" }
            LOGGER.debug { e.printStackTrace() }
            return ErrorResponse(error)
        }
    }

    private fun post(id: String, body: String): Response {
        val responseEntity = webClient
            .post()
            .uri { uriBuilder -> uriBuilder.path(id).build() }
            .bodyValue(body)
            .retrieve()
            .toEntity(String::class.java)
            .block(httpProps.responseTimeout)

        return when {
            responseEntity == null -> ErrorResponse("No response")
            responseEntity.body.isNullOrBlank() -> ErrorResponse("Empty response")
            else -> OkResponse(responseEntity.body!!)
        }
    }
}
