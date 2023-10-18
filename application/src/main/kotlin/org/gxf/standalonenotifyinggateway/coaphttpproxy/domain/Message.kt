// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.domain

import com.fasterxml.jackson.databind.JsonNode

class Message(val deviceId: String, val payload: JsonNode) {

    override fun toString() = "Message[ deviceId=\"$deviceId\", payload=\"${payload.toString()}\" ]"
}
