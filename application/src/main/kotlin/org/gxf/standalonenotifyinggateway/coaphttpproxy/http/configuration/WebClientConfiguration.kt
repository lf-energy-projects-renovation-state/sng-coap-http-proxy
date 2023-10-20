package org.gxf.standalonenotifyinggateway.coaphttpproxy.http.configuration

import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties.HttpProperties
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(private val httpProps: HttpProperties) {
    @Bean
    fun webClient(webClientBuilder: WebClient.Builder, webClientSsl: WebClientSsl): WebClient {
        return webClientBuilder
                .baseUrl("${httpProps.protocol}://${httpProps.url}/")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .apply(webClientSsl.fromBundle(httpProps.sslBundle))
                .build()
    }
}