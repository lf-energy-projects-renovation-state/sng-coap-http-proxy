package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.psk

import org.springframework.stereotype.Component

@Component
class PskStoreStub {

    fun retrieveAll() = listOf(Psk("867787050253370", "ABCDEFGHIJKLMNOP"))

}