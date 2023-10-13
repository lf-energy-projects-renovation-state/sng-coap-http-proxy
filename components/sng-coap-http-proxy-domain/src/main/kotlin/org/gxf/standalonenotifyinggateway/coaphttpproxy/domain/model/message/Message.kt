// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message

class Message(val deviceId: String, val payload: Payload) {

    override fun toString() = "Message[ deviceId=\"$deviceId\", payload=\"${payload}\" ]"
}
