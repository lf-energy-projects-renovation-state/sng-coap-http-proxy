package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "config.psk")
class PskStubProperties(val defaultId: String, val defaultKey: String)
