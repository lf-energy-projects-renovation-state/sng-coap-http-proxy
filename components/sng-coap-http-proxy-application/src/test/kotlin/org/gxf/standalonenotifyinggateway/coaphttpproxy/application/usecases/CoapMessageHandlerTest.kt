// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.application.usecases

import com.fasterxml.jackson.databind.json.JsonMapper
import org.assertj.core.api.Assertions.assertThat
import org.gxf.standalonenotifyinggateway.coaphttpproxy.application.ports.output.HttpMessageOutputPort
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Payload
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.ErrorResponse
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.InvalidMessageResponse
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.OkResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given

@ExtendWith(MockitoExtension::class)
class CoapMessageHandlerTest {

    private val jsonMapper = JsonMapper()

    private val id = "1234567890"
    private val validPayload = """{ "ID": "$id" }"""
    private val invalidPayload = """{ "ID": "other id" }"""

    private val validMessage = Message(id, Payload(messageAsJson(validPayload)))
    private val invalidMessage = Message(id, Payload(messageAsJson(invalidPayload)))

    private val okResponse = OkResponse("0")
    private val errorResponse = ErrorResponse("some failure occured")

    @Mock
    lateinit var httpClient: HttpMessageOutputPort

    @InjectMocks
    lateinit var messageHandler: CoapMessageHandler

    @Test
    fun shouldReturnOkResponse() {
        // arrange
        given(httpClient.post(validMessage)).willReturn(okResponse)

        // act
        val actual = messageHandler.handlePost(validMessage)

        // assert
        assertThat(actual).isEqualTo(okResponse)
    }

    @Test
    fun shouldReturnNotOkResponseWhenMessageIsInvalid() {
        // arrange
        // Nothing to arrange, http client should have no interactions

        // act
        val actual = messageHandler.handlePost(invalidMessage)

        // assert
        verifyNoInteractions(httpClient)
        assertThat(actual).isInstanceOf(InvalidMessageResponse::class.java)
    }

    @Test
    fun shouldReturnNotOkResponseWhenHttpClientReturnsNotOkResponse() {
        // arrange
        given(httpClient.post(validMessage)).willReturn(errorResponse)

        // act
        val actual = messageHandler.handlePost(validMessage)

        // assert
        assertThat(actual).isEqualTo(errorResponse)

    }

    private fun messageAsJson(message: String) = jsonMapper.readTree(message)

}
