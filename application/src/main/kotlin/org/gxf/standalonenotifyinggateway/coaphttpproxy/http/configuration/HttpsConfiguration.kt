// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration

import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties.HttpProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(value = [HttpProperties::class])
@ConditionalOnProperty(prefix = "config.http", name = ["sslEnabled"], havingValue = "true")
class HttpsConfiguration() {

    @Bean
    fun webClient(webClientBuilder: WebClient.Builder, webClientSsl: WebClientSsl, httpProps: HttpProperties) =
        webClientBuilder
            .baseUrl(baseUrl(httpProps))
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .apply(webClientSsl.fromBundle(httpProps.sslBundle))
            .build()

    private fun baseUrl(httpProps: HttpProperties) = "https://${httpProps.host}:${httpProps.port}/${httpProps.target}/"
}
