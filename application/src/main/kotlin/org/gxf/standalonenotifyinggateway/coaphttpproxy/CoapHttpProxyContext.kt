// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy

import org.gxf.standalonenotifyinggateway.coaphttpproxy.application.ports.output.HttpMessageOutputPort
import org.gxf.standalonenotifyinggateway.coaphttpproxy.application.usecases.CoapMessageHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoapHttpProxyContext {

    @Bean
    fun coapMessageInputPort(httpMessageOutputPort: HttpMessageOutputPort) =
        CoapMessageHandler(httpMessageOutputPort)
}
