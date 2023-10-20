package org.gxf.standalonenotifyinggateway.coaphttpproxy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.Fault
import org.eclipse.californium.core.coap.CoAP
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.coap.Request
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

    @Value("\${config.http.protocol}")
    private lateinit var protocol: String

    @Value("\${config.psk.default-id}")
    private lateinit var securityContextId: String

    @Autowired
    private lateinit var coapClient: IntegrationTestCoapClient

    private lateinit var wiremock: WireMockServer
    private val wiremockStubOk = WireMock.post(WireMock.urlPathTemplate("/sng-test/{id}")).willReturn(WireMock.ok("0"))
    private val wiremockStubError = WireMock.post(WireMock.urlPathTemplate("/sng-test/{id}")).willReturn(WireMock.aResponse().withFault(Fault.EMPTY_RESPONSE))

    @BeforeEach
    fun beforeEach() {
        val url = URL("$protocol://$url")
        wiremock = WireMockServer(url.port)
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

        val wiremockRequests = wiremock.findAll(WireMock.postRequestedFor(WireMock.urlPathTemplate("/sng-test/{id}")))

        Assertions.assertEquals(wiremockRequests.size, 1)
        Assertions.assertEquals(jsonNode, ObjectMapper().readTree(wiremockRequests.first().bodyAsString))
    }

    @Test
    fun shouldNotForwardCoapMessageToHttpWhenTheIdsDontMatch() {
        wiremock.stubFor(wiremockStubOk)

        val coapClient = coapClient.getClient()
        val jsonNode = ObjectMapper().readTree("{\"ID\": \"${securityContextId.plus("1")}\"}")

        val request =
                Request.newPost()
                        .apply {
                            options.setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
                        }.setPayload(CBORMapper().writeValueAsBytes(jsonNode))

        val response = coapClient.advanced(request)

        val wiremockRequests = wiremock.findAll(WireMock.postRequestedFor(WireMock.urlPathTemplate("/sng-test/{id}")))

        Assertions.assertEquals(0, wiremockRequests.size)
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

        val wiremockRequests = wiremock.findAll(WireMock.postRequestedFor(WireMock.urlPathTemplate("/sng-test/{id}")))

        Assertions.assertEquals(wiremockRequests.size, 1)
        Assertions.assertEquals(CoAP.ResponseCode.BAD_GATEWAY, response.code)
    }
}
