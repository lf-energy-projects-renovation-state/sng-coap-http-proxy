// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoapHttpProxyApplication

fun main(args: Array<String>) {
    runApplication<CoapHttpProxyApplication>(*args)
}
