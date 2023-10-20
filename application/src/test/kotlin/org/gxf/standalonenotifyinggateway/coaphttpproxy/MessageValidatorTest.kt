package org.gxf.standalonenotifyinggateway.coaphttpproxy

import com.fasterxml.jackson.databind.ObjectMapper
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.validation.MessageValidator
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MessageValidatorTest {

    @Test
    fun testMessageValidatorCorrectId() {
        val jsonNode = ObjectMapper().readTree("{\"ID\": 12345}")
        val testMessage = Message("12345", jsonNode)

        val result = MessageValidator().isValid(testMessage)

        Assertions.assertTrue(result)
    }

    @Test
    fun testMessageValidatorInvalidId() {
        val jsonNode = ObjectMapper().readTree("{\"ID\": 12345}")
        val testMessage = Message("6789", jsonNode)

        val result = MessageValidator().isValid(testMessage)

        Assertions.assertFalse(result)
    }
}
