// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.adapter.coap.test.client

import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.config.CoapConfig
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.elements.config.TcpConfig
import org.eclipse.californium.elements.config.UdpConfig
import org.eclipse.californium.scandium.DTLSConnector
import org.eclipse.californium.scandium.MdcConnectionListener
import org.eclipse.californium.scandium.config.DtlsConfig
import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.ProtocolVersion
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedPskStore
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedSinglePskStore
import org.springframework.context.annotation.Bean
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import org.eclipse.californium.elements.config.Configuration as CaliforniumConfiguration
import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
class CoapClientConfiguration {

    companion object {
        init {
            DtlsConfig.register()
            CoapConfig.register()
            UdpConfig.register()
            TcpConfig.register()
        }

        const val COAP_URI = "coaps://localhost:55684/sng"
        const val TEST_ID = "867787050253370"
        private const val TEST_KEY = "ABCDEFGHIJKLMNOP"
        private val PSK_STORE = AdvancedSinglePskStore(TEST_ID, TEST_KEY.toByteArray())
        private const val INVALID_TEST_KEY = "ABCD"
        private val INVALID_PSK_STORE = AdvancedSinglePskStore(TEST_ID, INVALID_TEST_KEY.toByteArray())
    }

    @Bean("invalidCoapClient")
    fun invalidCoapClient(clientConfiguration: CaliforniumConfiguration) = CoapClient(COAP_URI)
        .setEndpoint(
            CoapEndpoint.Builder()
                .setConfiguration(clientConfiguration)
                .setConnector(createDtlsConnector(clientConfiguration, 54322, INVALID_PSK_STORE))
                .build()
        )

    @Bean("validCoapClient")
    fun coapClient(clientConfiguration: CaliforniumConfiguration) = CoapClient(COAP_URI)
        .setEndpoint(
            CoapEndpoint.Builder()
                .setConfiguration(clientConfiguration)
                .setConnector(createDtlsConnector(clientConfiguration, 54321, PSK_STORE))
                .build()
        )


    private fun createDtlsConnector(
        clientConfiguration: CaliforniumConfiguration,
        port: Int,
        pskStore: AdvancedPskStore
    ) = DTLSConnector(
        DtlsConnectorConfig.builder(clientConfiguration)
            .setAddress(InetSocketAddress(port))
            .setAdvancedPskStore(pskStore)
            .setConnectionListener(MdcConnectionListener())
            .setProtocolVersionForHelloVerifyRequests(ProtocolVersion.VERSION_DTLS_1_2)
            .build()
    )

    @Bean("clientConfiguration")
    fun clientConfiguration() =
        CaliforniumConfiguration.getStandard()
            .set(CoapConfig.COAP_SECURE_PORT, 55686)
            .set(CoapConfig.ACK_TIMEOUT, 500, TimeUnit.MILLISECONDS)
            .set(CoapConfig.MAX_ACK_TIMEOUT, 500, TimeUnit.MILLISECONDS)
            .set(DtlsConfig.DTLS_ROLE, DtlsRole.CLIENT_ONLY)
            .set(DtlsConfig.DTLS_PRESELECTED_CIPHER_SUITES, listOf(CipherSuite.TLS_PSK_WITH_AES_256_CCM_8))
            .set(DtlsConfig.DTLS_CIPHER_SUITES, listOf(CipherSuite.TLS_PSK_WITH_AES_256_CCM_8))
            .set(DtlsConfig.DTLS_RETRANSMISSION_BACKOFF, 0)
            .set(DtlsConfig.DTLS_RETRANSMISSION_TIMEOUT, 500, TimeUnit.MILLISECONDS)
            .set(DtlsConfig.DTLS_MAX_RETRANSMISSION_TIMEOUT, 500, TimeUnit.MILLISECONDS)
            .set(DtlsConfig.DTLS_MAX_RETRANSMISSIONS, 2)

}
