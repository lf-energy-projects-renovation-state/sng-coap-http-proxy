// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.standalonenotifyinggateway.coaphttpproxy.http.configuration

import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.configuration.properties.HttpProperties
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(private val httpProps: HttpProperties) {
  @Bean
  fun webClient(webClientBuilder: WebClient.Builder, webClientSsl: WebClientSsl): WebClient {
    return webClientBuilder
        .baseUrl(httpProps.url)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .apply {
          if (httpProps.sslBundle != null) {
            it.apply(webClientSsl.fromBundle(httpProps.sslBundle))
          }
        }
        .build()
  }
}
