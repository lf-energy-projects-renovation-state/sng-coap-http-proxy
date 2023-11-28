// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.Fault
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.coap.Request
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.PskStubProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.configuration.properties.HttpProperties
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.net.URL

@Import(IntegrationTestCoapClient::class)
@EnableConfigurationProperties(PskStubProperties::class, HttpProperties::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @Autowired
    private lateinit var coapClient: IntegrationTestCoapClient

    @Autowired
    private lateinit var pskStubProperties: PskStubProperties

    @Autowired
    private lateinit var httpProperties: HttpProperties

    private lateinit var wiremock: WireMockServer

    private val wiremockStubOk = post(urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")).willReturn(ok("0"))
    private val wiremockStubError = post(urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")).willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE))
    private val wiremockStubErrorEndpoint = post(urlPathTemplate(HttpClient.ERROR_PATH)).willReturn(aResponse().withStatus(200))
    private lateinit var jsonNode: JsonNode

    @BeforeEach
    fun beforeEach() {
        val wiremockStubPsk = get(urlPathTemplate(HttpClient.PSK_PATH)).willReturn(ok(pskStubProperties.defaultKey))

        jsonNode = ObjectMapper().readTree(""" 
            {
                "ID": "${pskStubProperties.defaultId}"
            }
            """)

        val url = URL(httpProperties.url)
        wiremock = WireMockServer(url.port)
        wiremock.stubFor(wiremockStubErrorEndpoint)
        wiremock.stubFor(wiremockStubOk)
        wiremock.stubFor(wiremockStubPsk)
        wiremock.start()
    }

    @AfterEach
    fun afterEach() {
        wiremock.stop()
    }

    @Test
    fun shouldForwardCoapMessageToHttp() {
        val coapClient = coapClient.getClient()

        val request =
                Request.newPost()
                        .apply {
                            options.setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
                        }.setPayload(CBORMapper().writeValueAsBytes(jsonNode))

        coapClient.advanced(request)

        val wiremockRequests = wiremock.findAll(postRequestedFor(urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")))

        assertThat(wiremockRequests).hasSize(1)
        assertThat(jsonNode).isEqualTo(ObjectMapper().readTree(wiremockRequests.first().bodyAsString))
    }

    // When a error occurs should not forward the coap message to the next service
    // Instead it should call the error endpoint with the error
    @Test
    fun shouldNotForwardCoapMessageToHttpWhenTheIdsDontMatch() {
        val coapClient = coapClient.getClient()

        val jsonNodeWithInvalidId = (jsonNode as ObjectNode)
                .put("ID", jsonNode.findValue("ID").asText().plus("1"))

        val request =
                Request.newPost()
                        .apply {
                            options.setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
                        }.setPayload(CBORMapper().writeValueAsBytes(jsonNodeWithInvalidId))

        val response = coapClient.advanced(request)

        val wiremockRequestsSng = wiremock.findAll(postRequestedFor(urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")))
        val wiremockRequestsError = wiremock.findAll(postRequestedFor(urlPathTemplate(HttpClient.ERROR_PATH)))

        assertThat(wiremockRequestsSng).hasSize(0)
        assertThat(wiremockRequestsError).hasSize(1)
        assertThat(CoAP.ResponseCode.BAD_GATEWAY).isEqualTo(response.code)
    }

    @Test
    fun shouldReturnBadGatewayWhenHttpClientReturnsUnexpectedError() {
        wiremock.stubFor(wiremockStubError)

        val coapClient = coapClient.getClient()

        val request =
                Request.newPost()
                        .apply {
                            options.setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
                        }.setPayload(CBORMapper().writeValueAsBytes(jsonNode))

        val response = coapClient.advanced(request)

        val wiremockRequests = wiremock.findAll(postRequestedFor(urlPathTemplate("${HttpClient.MESSAGE_PATH}/{id}")))
        val wiremockRequestsErrorEndpoint = wiremock.findAll(postRequestedFor(urlPathEqualTo(HttpClient.ERROR_PATH)))


        assertThat(wiremockRequestsErrorEndpoint).hasSize(1)
        assertThat(wiremockRequests).hasSize(1)
        assertThat(CoAP.ResponseCode.BAD_GATEWAY).isEqualTo(response.code)
    }
}
