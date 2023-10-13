// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.domain.model.response

open class ErrorResponse(val error: String) : Response {

    override fun toString() = "ErrorResponse [ error=\"$error\" ]"
}
