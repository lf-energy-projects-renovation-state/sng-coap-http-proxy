// SPDX-FileCoponfigyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration

import org.eclipse.californium.core.config.CoapConfig
import org.eclipse.californium.elements.config.CertificateAuthenticationMode
import org.eclipse.californium.elements.config.SystemConfig
import org.eclipse.californium.elements.config.TcpConfig
import org.eclipse.californium.elements.config.UdpConfig
import org.eclipse.californium.scandium.config.DtlsConfig
import org.eclipse.californium.scandium.config.DtlsConfig.DtlsRole
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite.TLS_PSK_WITH_AES_256_CCM_8
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedMultiPskStore
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.CoapProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.properties.UdpProperties
import org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.psk.PskStoreStub
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import java.util.concurrent.TimeUnit
import org.eclipse.californium.elements.config.Configuration as CaliforniumConfiguration
import org.springframework.context.annotation.Configuration as SpringConfiguration

@SpringConfiguration
@EnableConfigurationProperties(value = [CoapProperties::class, UdpProperties::class])
class CoapConfiguration(private val coapProps: CoapProperties, private val udpProps: UdpProperties) {

    init {
        DtlsConfig.register()
        CoapConfig.register()
        UdpConfig.register()
        TcpConfig.register()
    }

    @Bean
    fun pskStore(pskRepository: PskStoreStub) = AdvancedMultiPskStore()
            .apply {
                pskRepository.retrieveAll().forEach { psk -> this.setKey(psk.id, psk.key.toByteArray()) }
            }

    @Bean
    fun serverConfiguration() =
            CaliforniumConfiguration.getStandard()
                    .apply {
                        updateCoapConfigFromProperties(this)
                        updateUdpConfigFromProperties(this)
                        updateDtlsConfigFromProperties(this)
                    }

    fun updateCoapConfigFromProperties(config: CaliforniumConfiguration) {
        with(config) {
            set(CoapConfig.MAX_ACTIVE_PEERS, coapProps.maxActivePeers)
            set(CoapConfig.MAX_RESOURCE_BODY_SIZE, coapProps.maxResourceBodySize)
            set(CoapConfig.MAX_MESSAGE_SIZE, coapProps.maxMessageSize)
            set(CoapConfig.PREFERRED_BLOCK_SIZE, coapProps.preferredBlockSize)
            set(CoapConfig.DEDUPLICATOR, coapProps.deduplicator)
            set(CoapConfig.COAP_PORT, coapProps.coapsPort)
            set(
                    CoapConfig.MAX_PEER_INACTIVITY_PERIOD,
                    coapProps.maxPeerInactivityPeriod.getSeconds(),
                    TimeUnit.SECONDS
            )
        }
    }

    fun updateUdpConfigFromProperties(config: CaliforniumConfiguration) {
        with(config) {
            set(UdpConfig.UDP_RECEIVE_BUFFER_SIZE, udpProps.udpReceiveBufferSize);
            set(UdpConfig.UDP_SEND_BUFFER_SIZE, udpProps.udpSendBufferSize);
            set(
                    SystemConfig.HEALTH_STATUS_INTERVAL,
                    udpProps.healthStatusInterval.getSeconds(),
                    TimeUnit.SECONDS
            )
        }
    }

    fun updateDtlsConfigFromProperties(config: CaliforniumConfiguration) {
        with(config) {
            set(DtlsConfig.DTLS_ROLE, DtlsRole.SERVER_ONLY);
            set(DtlsConfig.DTLS_RECOMMENDED_CIPHER_SUITES_ONLY, false);
            set(DtlsConfig.DTLS_PRESELECTED_CIPHER_SUITES, listOf(TLS_PSK_WITH_AES_256_CCM_8));
            set(DtlsConfig.DTLS_CIPHER_SUITES, listOf(TLS_PSK_WITH_AES_256_CCM_8));
            set(DtlsConfig.DTLS_CLIENT_AUTHENTICATION_MODE, CertificateAuthenticationMode.NONE)
        }
    }
}