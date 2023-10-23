package org.gxf.standalonenotifyinggateway.coaphttpproxy

import com.fasterxml.jackson.databind.ObjectMapper
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.MessageHandler
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.exception.InvalidMessageException
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.validation.MessageValidator
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MessageHandlerTest {

    @Mock
    private lateinit var httpClient: HttpClient

    @Mock
    private lateinit var messageValidator: MessageValidator

    @InjectMocks
    private lateinit var messageHandler: MessageHandler

    private val testJsonNode = ObjectMapper().readTree("{\"ID\": 12345}")

    @Test
    fun shouldThrowWhenInvalidMessage() {
        val message = Message("12345", testJsonNode)

        Mockito.`when`(messageValidator.isValid(message)).thenReturn(false)

        Assertions.assertThrows(InvalidMessageException::class.java) {
            messageHandler.handlePost(message)
        }
    }

    @Test
    fun callHttpClientWhenMessageIsValid() {
        val message = Message("12345", testJsonNode)

        Mockito.`when`(messageValidator.isValid(message)).thenReturn(true)

        messageHandler.handlePost(message)

        Mockito.verify(httpClient).post(message)
    }
}
