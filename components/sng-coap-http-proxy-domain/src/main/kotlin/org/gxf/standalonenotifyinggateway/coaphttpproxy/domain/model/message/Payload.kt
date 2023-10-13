// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.message

import com.fasterxml.jackson.databind.JsonNode

class Payload(val jsonNode: JsonNode) {

    val jsonString = jsonNode.asText()

    override fun toString() = "Payload [ jsonString:\"$jsonString\" ]"
}
