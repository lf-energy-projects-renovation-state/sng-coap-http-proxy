// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.standalonenotifyinggateway.coaphttpproxy.logging

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.configuration.properties.HttpProperties
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RemoteLoggingWebClient(
    private val webClient: RestClient,
    private val httpProperties: HttpProperties,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * The web connection is crucial and should be tested on startup. If for any reason (e.g. TLS) this connection
     * fails, you will immediately see it in de logs. If an error occurs once the first device connects, the error might
     * be invisible, because the error cannot be posted.
     */
    @PostConstruct
    fun checkWebConnection() {
        if (httpProperties.connectionCheck) {
            try {
                executeErrorRequest("CoAP proxy started")
            } catch (e: Exception) {
                logger.error {
                    """The device-service could not be reached. Make sure
                - The device-service is running
                - The TLS connection is OK (in case of new certificates, copy them manually)
            """
                        .trimMargin()
                }
                throw e
            }
        }
    }

    fun remoteLogMessage(message: String) {
        executeErrorRequest(message)
    }

    private fun executeErrorRequest(body: String) {
        webClient.post().uri(HttpClient.ERROR_PATH).body(body).retrieve()
    }
}
