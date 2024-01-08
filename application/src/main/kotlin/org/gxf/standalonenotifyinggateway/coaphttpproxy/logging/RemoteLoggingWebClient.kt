package org.gxf.standalonenotifyinggateway.coaphttpproxy.logging

import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Component
class RemoteLoggingWebClient(private val webClient: WebClient) {

    fun remoteLogMessage(message: String) {
        executeErrorRequest(message)
    }

    private fun executeErrorRequest(body: String) {
        webClient
                .post()
                .uri(HttpClient.ERROR_PATH)
                .bodyValue(body)
                .retrieve()
                .bodyToMono<Unit>()
                .subscribe()
    }
}
