// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.Fault
import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.coap.Request
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.net.URL
import java.util.*

@Import(IntegrationTestCoapClient::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @Value("\${config.http.url}")
    private lateinit var url: String

    @Value("\${config.psk.default-id}")
    private lateinit var securityContextId: String

    @Autowired
    private lateinit var coapClient: IntegrationTestCoapClient

    private lateinit var wiremock: WireMockServer
    private val wiremockStubOk = WireMock.post(WireMock.urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")).willReturn(WireMock.ok("0"))
    private val wiremockStubError = WireMock.post(WireMock.urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")).willReturn(WireMock.aResponse().withFault(Fault.EMPTY_RESPONSE))
    private val wiremockStubErrorEndpoint = WireMock.post(WireMock.urlPathTemplate(HttpClient.ERROR_PATH)).willReturn(WireMock.aResponse().withStatus(200))

    @BeforeEach
    fun beforeEach() {
        val url = URL(url)
        wiremock = WireMockServer(url.port)
        wiremock.stubFor(wiremockStubErrorEndpoint)
        wiremock.start()
    }

    @AfterEach
    fun afterEach() {
        wiremock.stop()
    }

    @Test
    fun shouldForwardCoapMessageToHttp() {
        wiremock.stubFor(wiremockStubOk)

        val coapClient = coapClient.getClient()
        val jsonNode = ObjectMapper().readTree("{\"ID\": \"$securityContextId\"}")

        val request =
                Request.newPost()
                        .apply {
                            options.setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
                        }.setPayload(CBORMapper().writeValueAsBytes(jsonNode))

        coapClient.advanced(request)

        val wiremockRequests = wiremock.findAll(WireMock.postRequestedFor(WireMock.urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")))

        Assertions.assertEquals(wiremockRequests.size, 1)
        Assertions.assertEquals(jsonNode, ObjectMapper().readTree(wiremockRequests.first().bodyAsString))
    }

    // When a error occurs should not forward the coap message to the next service
    // Instead it should call the error endpoint with the error
    @Test
    fun shouldNotForwardCoapMessageToHttpWhenTheIdsDontMatch() {
        val coapClient = coapClient.getClient()
        val jsonNode = ObjectMapper().readTree("{\"ID\": \"${securityContextId.plus("1")}\"}")

        val request =
                Request.newPost()
                        .apply {
                            options.setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
                        }.setPayload(CBORMapper().writeValueAsBytes(jsonNode))

        val response = coapClient.advanced(request)

        val wiremockRequestsSng = wiremock.findAll(WireMock.postRequestedFor(WireMock.urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")))
        val wiremockRequestsError = wiremock.findAll(WireMock.postRequestedFor(WireMock.urlPathTemplate(HttpClient.ERROR_PATH)))


        Assertions.assertEquals(0, wiremockRequestsSng.size)
        Assertions.assertEquals(1, wiremockRequestsError.size)
        Assertions.assertEquals(CoAP.ResponseCode.BAD_GATEWAY, response.code)
    }

    @Test
    fun shouldReturnBadGatewayWhenHttpClientReturnsUnexpectedError() {
        wiremock.stubFor(wiremockStubError)

        val coapClient = coapClient.getClient()
        val jsonNode = ObjectMapper().readTree("{\"ID\": \"${securityContextId}\"}")

        val request =
                Request.newPost()
                        .apply {
                            options.setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
                        }.setPayload(CBORMapper().writeValueAsBytes(jsonNode))

        val response = coapClient.advanced(request)

        val wiremockRequests = wiremock.findAll(WireMock.postRequestedFor(WireMock.urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")))
        val wiremockRequestsErrorEndpoint = wiremock.findAll(WireMock.postRequestedFor(WireMock.urlPathTemplate(HttpClient.ERROR_PATH)))


        Assertions.assertEquals(wiremockRequests.size, 1)
        Assertions.assertEquals(wiremockRequestsErrorEndpoint.size, 1)
        Assertions.assertEquals(CoAP.ResponseCode.BAD_GATEWAY, response.code)
    }
}
