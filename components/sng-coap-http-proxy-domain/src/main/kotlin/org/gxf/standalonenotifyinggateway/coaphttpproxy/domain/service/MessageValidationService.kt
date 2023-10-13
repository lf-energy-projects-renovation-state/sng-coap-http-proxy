// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.service

import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message.Message

object MessageValidationService {

    fun isValid(message: Message) = checkIDs(message)

    private fun checkIDs(message: Message) = message.deviceId.equals(idFromPayload(message))

    private fun idFromPayload(message: Message) = message.payload.jsonNode.findValue("ID").asText()
}
