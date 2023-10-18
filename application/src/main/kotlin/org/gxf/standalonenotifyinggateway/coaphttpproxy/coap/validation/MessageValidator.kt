// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.validation

import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.Message

object MessageValidator {

    fun isValid(message: Message) = checkIDs(message)

    private fun checkIDs(message: Message) = message.deviceId == idFromPayload(message)

    private fun idFromPayload(message: Message) = message.payload.findValue("ID").asText()
}
