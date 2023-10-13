// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.application.ports.output

import org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.psk.Psk

interface PskRepositoryOutputPort {
    fun retrieveAll(): List<Psk>
}
