// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration

import io.netty.channel.ChannelOption
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties.HttpProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
@EnableConfigurationProperties(value = [HttpProperties::class])
@ConditionalOnProperty(prefix = "config.http", name = ["sslEnabled"], havingValue = "false")
class HttpConfiguration() {

    @Bean
    fun webClient(httpProps: HttpProperties) = WebClient.builder()
        .clientConnector(ReactorClientHttpConnector(httpClient(httpProps)))
        .baseUrl(baseUrl(httpProps))
        .defaultHeader(HttpHeaders.ACCEPT, "application/json")
        .build()

    private fun httpClient(httpProps: HttpProperties) = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, httpProps.connectionTimeout.toMillis().toInt())

    private fun baseUrl(httpProps: HttpProperties) = "http://${httpProps.host}:${httpProps.port}/${httpProps.target}/"

}
