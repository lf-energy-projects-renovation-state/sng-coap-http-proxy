// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response

class OkResponse(val body: String) : Response {

    override fun toString() = "OkResponse [ body=\"$body\" ]"
}
