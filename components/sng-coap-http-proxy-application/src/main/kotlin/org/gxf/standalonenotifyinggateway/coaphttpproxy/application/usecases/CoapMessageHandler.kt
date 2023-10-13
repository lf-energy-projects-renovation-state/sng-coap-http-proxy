// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.application.usecases

import mu.KotlinLogging
import org.gxf.standalonenotifyinggateway.coaphttpproxy.application.ports.input.CoapMessageInputPort
import org.gxf.standalonenotifyinggateway.coaphttpproxy.application.ports.output.HttpMessageOutputPort
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Message
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.InvalidMessageResponse
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response.Response
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.service.MessageValidationService

class CoapMessageHandler(val httpMessageOutputPort: HttpMessageOutputPort) : CoapMessageInputPort {

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }

    override fun handlePost(message: Message): Response {
        LOGGER.debug { "Handling post, for message: $message." }

        if (MessageValidationService.isValid(message)) {
            val response = httpMessageOutputPort.post(message)
            LOGGER.debug { "Handled post, got response: $response." }
            return response
        }

        LOGGER.warn { "Received invalid message: $message" }
        return InvalidMessageResponse()
    }

}
