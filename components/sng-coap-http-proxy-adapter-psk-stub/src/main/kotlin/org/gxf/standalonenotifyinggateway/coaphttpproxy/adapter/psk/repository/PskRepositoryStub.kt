// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.psk.repository

import org.gxf.standalonenotifyinggateway.coaphttpproxy.application.ports.output.PskRepositoryOutputPort
import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.psk.Psk
import org.springframework.stereotype.Component

@Component
class PskRepositoryStub : PskRepositoryOutputPort {

    override fun retrieveAll() = listOf(Psk("867787050253370", "ABCDEFGHIJKLMNOP"))

}
