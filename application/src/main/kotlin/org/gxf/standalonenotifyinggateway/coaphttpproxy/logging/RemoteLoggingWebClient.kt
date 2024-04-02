package org.gxf.standalonenotifyinggateway.coaphttpproxy.logging

import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RemoteLoggingWebClient(private val webClient: RestClient) {

    fun remoteLogMessage(message: String) {
        executeErrorRequest(message)
    }

    private fun executeErrorRequest(body: String) {
        webClient
                .post()
                .uri(HttpClient.ERROR_PATH)
            .body(body)
                .retrieve()
    }
}
