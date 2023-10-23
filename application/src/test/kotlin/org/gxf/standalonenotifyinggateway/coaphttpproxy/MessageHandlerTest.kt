package org.gxf.standalonenotifyinggateway.coaphttpproxy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.MessageHandler
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.InvalidMessageException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.validation.MessageValidator
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.check


@ExtendWith(MockitoExtension::class)
class MessageHandlerTest {

    @Mock
    private lateinit var httpClient: HttpClient

    @Mock
    private lateinit var messageValidator: MessageValidator

    @InjectMocks
    private lateinit var messageHandler: MessageHandler

    private val testJsonNode = ObjectMapper().readTree("{\"ID\": 12345}")
    private val testCbor = CBORMapper().writeValueAsBytes(testJsonNode)

    @Test
    fun shouldThrowWhenInvalidMessage() {
        Mockito.`when`(messageValidator.isValid(any<Message>())).thenReturn(false)

        Assertions.assertThrows(InvalidMessageException::class.java) {
            messageHandler.handlePost("12345", testCbor)
        }
    }

    @Test
    fun callHttpClientWhenMessageIsValid() {
        val message = Message("12345", testJsonNode)

        Mockito.`when`(messageValidator.isValid(any<Message>())).thenReturn(true)

        messageHandler.handlePost("12345", testCbor)

        Mockito.verify(httpClient).post(check {
            Assertions.assertEquals(message.deviceId, it.deviceId)
            Assertions.assertEquals(message.payload, it.payload)
        })
    }
}
