// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.CoAP.ResponseCode
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.coap.Request
import org.eclipse.californium.core.coap.Response
import org.eclipse.californium.scandium.dtls.DtlsHandshakeTimeoutException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.coap.test.client.CoapClientConfiguration.Companion.COAP_URI
import org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.coap.test.client.CoapClientConfiguration.Companion.TEST_ID
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.springtest.MockServerTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.http.HttpMethod
import java.io.IOException


@MockServerTest("config.http.port=\${mockServerPort}")
@SpringBootTest
@TestConfiguration
class CoapHttpProxyApplicationTests {

    companion object {

        private val LOGGER = KotlinLogging.logger { }

        private const val JSON_VALID_MESSAGE = """{ "ID": "$TEST_ID" }"""
        private const val JSON_INVALID_MESSAGE = """{ "ID": "invalid id" }"""
        private val OK_HTTP_RESPONSE = HttpResponse.response("0")
        private val NOT_OK_HTTP_RESPONSE = HttpResponse.notFoundResponse()
        private val OK_COAP_RESPONSE = Response(ResponseCode.CONTENT).apply { setPayload("0") }
        private val NOT_OK_COAP_RESPONSE = Response(ResponseCode.BAD_GATEWAY).apply { setPayload("") }
    }

    private val jsonMapper = JsonMapper()
    private val cborMapper = CBORMapper()

    private lateinit var mockServerClient: MockServerClient
    private lateinit var response: CoapResponse
    private lateinit var throwable: Throwable

    private lateinit var coapClient: CoapClient

    @Autowired
    @Qualifier("validCoapClient")
    private lateinit var client: CoapClient

    @Autowired
    @Qualifier("invalidCoapClient")
    private lateinit var invalidClient: CoapClient


    @Test
    fun testScenarioProxyReceivesValidMessage() {
        // given
        givenACoapClient()
        givenTheHttpServerRespondsWith(OK_HTTP_RESPONSE)

        // when
        whenTheCoapClientSendsMessage(JSON_VALID_MESSAGE)

        // then
        thenTheCoapClientShouldReceive(OK_COAP_RESPONSE)
    }

    @Test
    fun testScenarioProxyReceivesInvalidMessage() {
        // given
        givenACoapClient()

        // when
        whenTheCoapClientSendsMessage(JSON_INVALID_MESSAGE)

        // then
        thenTheCoapClientShouldReceive(NOT_OK_COAP_RESPONSE)
    }

    @Test
    fun testScenarioProxyReceivesValidMessageButHttpServerRespondsWithError() {
        // given
        givenACoapClient()
        givenTheHttpServerRespondsWith(NOT_OK_HTTP_RESPONSE)

        // when
        whenTheCoapClientSendsMessage(JSON_VALID_MESSAGE)

        // then
        thenTheCoapClientShouldReceive(NOT_OK_COAP_RESPONSE)
    }

    @Test
    fun testScenarioProxyReceivesInvalidConnectionAttempt() {
        // given
        givenAnInvalidCoapClient()
        givenTheHttpServerRespondsWith(NOT_OK_HTTP_RESPONSE)

        // when
        whenTheCoapClientAttemptsToSendMessage(JSON_VALID_MESSAGE)

        // then
        thenTheCoapClientShouldNotBeAbleToConnect()
    }

    private fun givenACoapClient() {
        coapClient = client
    }

    private fun givenAnInvalidCoapClient() {
        coapClient = invalidClient
    }

    private fun givenTheHttpServerRespondsWith(response: HttpResponse) {
        mockServerClient
            .`when`(HttpRequest.request(".*/sng/$TEST_ID").withMethod(HttpMethod.POST.toString()))
            .respond(response)
    }

    private fun whenTheCoapClientSendsMessage(message: String) {
        response = coapClient.advanced(request(message))
    }

    private fun whenTheCoapClientAttemptsToSendMessage(message: String) {
        throwable = catchThrowable { response = coapClient.advanced(request(message)) }
    }

    private fun thenTheCoapClientShouldReceive(expectedResponse: Response) {
        assertThat(response.getCode()).isEqualTo(expectedResponse.getCode())
        assertThat(response.getResponseText()).isEqualTo(expectedResponse.getPayloadString())
    }

    private fun thenTheCoapClientShouldNotBeAbleToConnect() {
        LOGGER.info { "Checking throwable: $throwable" }
        assertThat(throwable)
            .isInstanceOf(IOException::class.java)
            .cause()
            .isInstanceOf(DtlsHandshakeTimeoutException::class.java)
    }

    private fun request(message: String) = Request.newPost()
        .apply {
            this.setPayload(cborMapper.writeValueAsBytes(jsonMapper.readTree(message)))
            this.setURI(COAP_URI)
            this.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
            LOGGER.info { "Created request: $this" }
        }

}
