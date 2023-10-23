// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.psk

import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.PskStubProperties
import org.springframework.stereotype.Component

@Component
class PskStoreStub(private val pskStubProperties: PskStubProperties) {
    fun retrieveAll() = listOf(Psk(pskStubProperties.defaultId, pskStubProperties.defaultKey))
}
