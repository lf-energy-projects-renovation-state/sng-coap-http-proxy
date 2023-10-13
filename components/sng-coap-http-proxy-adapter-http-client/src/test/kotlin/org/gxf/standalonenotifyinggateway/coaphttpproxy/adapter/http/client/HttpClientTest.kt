// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client

import com.fasterxml.jackson.databind.json.JsonMapper
import org.assertj.core.api.Assertions.assertThat
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.HttpConfiguration
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.HttpsConfiguration
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Payload
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.OkResponse
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.springtest.MockServerTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.ErrorResponse
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.http.client.configuration.properties.HttpProperties
import org.mockserver.model.HttpError
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
@EnableAutoConfiguration
@SpringJUnitConfig(
    value = [HttpClient::class, HttpConfiguration::class, HttpsConfiguration::class],
    initializers = [ConfigDataApplicationContextInitializer::class]
)
@MockServerTest("config.http.port=\${mockServerPort}")
class HttpClientTest {

    companion object {
        private const val TEST_ID = "1234567890"
        private const val TEST_BODY = """{ "ID": "$TEST_ID" }"""
        private const val DEFAULT_RESPONSE_BODY = "0"
        private val DEFAULT_RESPONSE = HttpResponse.response(DEFAULT_RESPONSE_BODY)
        private val ERROR_404_RESPONSE = HttpResponse.notFoundResponse()
        private val EMPTY_RESPONSE = HttpResponse.response()
        private val NO_RESPONSE = HttpResponse.response().withDelay(TimeUnit.SECONDS, 30)

        private val jsonMapper = JsonMapper()
    }

    private val message = Message(TEST_ID, Payload(jsonMapper.readTree(TEST_BODY)))

    private lateinit var mockServerClient: MockServerClient

    @Autowired
    lateinit var httpProps: HttpProperties

    @Autowired
    lateinit var httpClient: HttpClient

    @Test
    fun shouldReturnOkResponse() {
        // arrange
        givenTheHttpServerRespondsWith(DEFAULT_RESPONSE)

        // act
        val actual = httpClient.post(message)

        // assert
        assertThat(actual).isInstanceOfSatisfying(
            OkResponse::class.java,
            { assertThat(it.body).isEqualTo(DEFAULT_RESPONSE_BODY) }
        )
    }

    @Test
    fun shouldReturnErrorResponseWhenHttpServerReturnsAnError() {
        // arrange
        givenTheHttpServerRespondsWith(ERROR_404_RESPONSE)
        val expectedError = "404 Not Found from POST ${url()}"

        // act
        val actual = httpClient.post(message)

        // assert
        assertThat(actual).isInstanceOfSatisfying(
            ErrorResponse::class.java,
            { assertThat(it.error).isEqualTo(expectedError) }
        )
    }

    @Test
    fun shouldReturnErrorResponseWhenHttpServerDoesNotRespondInTime() {
        // arrange
        givenTheHttpServerRespondsWith(NO_RESPONSE)
        val expectedError = "Timeout on blocking read for ${httpProps.responseTimeout.getNano()} NANOSECONDS"

        // act
        val actual = httpClient.post(message)

        // assert
        assertThat(actual).isInstanceOfSatisfying(
            ErrorResponse::class.java,
            { assertThat(it.error).isEqualTo(expectedError) }
        )
    }

    @Test
    fun shouldReturnErrorResponseWhenHttpServerReturnsAnEmptyResponse() {
        // arrange
        givenTheHttpServerRespondsWith(EMPTY_RESPONSE)

        // act
        val actual = httpClient.post(message)

        // assert
        assertThat(actual).isInstanceOfSatisfying(
            ErrorResponse::class.java,
            { assertThat(it.error).isEqualTo("Empty response") }
        )
    }

    @Test
    fun shouldReturnErrorResponseWhenHttpServerDoesNotRespond() {
        // arrange
        givenTheHttpServerDoesNotRespond()
        val expectedError = "Connection prematurely closed BEFORE response"

        // act
        val actual = httpClient.post(message)

        // assert
        assertThat(actual).isInstanceOfSatisfying(
            ErrorResponse::class.java,
            { assertThat(it.error).isEqualTo(expectedError) }
        )
    }

    private fun givenTheHttpServerRespondsWith(response: HttpResponse) {
        mockServerClient
            .`when`(HttpRequest.request(".*").withMethod(HttpMethod.POST.toString()))
            .respond(response)
    }

    private fun givenTheHttpServerDoesNotRespond() {
        mockServerClient
            .`when`(HttpRequest.request(".*"))
            .error(HttpError().withDropConnection(true))
    }

    private fun url() = "${if (httpProps.sslEnabled) "https" else "http"}://${httpProps.host}:${httpProps.port}/${httpProps.target}/$TEST_ID"

}
