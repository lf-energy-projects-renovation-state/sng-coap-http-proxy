// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.validation

import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message
import org.springframework.stereotype.Service

@Service
class MessageValidator {
    fun isValid(message: Message): Boolean {
        val payloadId = message.payload.findValue("ID").asText()
        return message.deviceId == payloadId
    }
}
