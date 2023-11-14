package org.gxf.standalonenotifyinggateway.coaphttpproxy.coap.configuration.psk

import org.eclipse.californium.scandium.dtls.ConnectionId
import org.eclipse.californium.scandium.dtls.HandshakeResultHandler
import org.eclipse.californium.scandium.dtls.PskPublicInformation
import org.eclipse.californium.scandium.dtls.PskSecretResult
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedPskStore
import org.eclipse.californium.scandium.util.SecretUtil
import org.eclipse.californium.scandium.util.ServerNames
import org.gxf.standalonenotifyinggateway.coaphttpproxy.http.HttpClient.Companion.PSK_PATH
import org.gxf.standalonenotifyinggateway.coaphttpproxy.logging.RemoteLogger
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.InetSocketAddress
import java.time.Duration
import javax.crypto.SecretKey


@Component
class RemotePskStore(private val webClient: WebClient, private val remoteLogger: RemoteLogger) : AdvancedPskStore {

    override fun hasEcdhePskSupported(): Boolean {
        return true
    }

    override fun requestPskSecretResult(cid: ConnectionId?, serverName: ServerNames?, identity: PskPublicInformation, hmacAlgorithm: String?, otherSecret: SecretKey?, seed: ByteArray?, useExtendedMasterSecret: Boolean): PskSecretResult {
        return PskSecretResult(cid, identity, getSecretForIdentity(identity.publicInfoAsString))
    }

    override fun getIdentity(peerAddress: InetSocketAddress, virtualHost: ServerNames?): PskPublicInformation? {
        throw NotImplementedError("Method not implemented because it is not used")
    }

    override fun setResultHandler(resultHandler: HandshakeResultHandler?) {
        // Empty implementation because we don't use Async handler
    }

    private fun getSecretForIdentity(identity: String): SecretKey? {
        val response = getKeyForIdentity(identity)
        val body = response?.body

        if (body.isNullOrEmpty()) {
            remoteLogger.error { "No key in body for identity: $identity" }
            return null
        }

        return SecretUtil.create(body.toByteArray(), PskSecretResult.ALGORITHM_PSK)
    }

    private fun getKeyForIdentity(identity: String): ResponseEntity<String>? {
        return webClient
                .get()
                .uri(PSK_PATH)
                .header("x-device-identity", identity)
                .retrieve()
                .toEntity(String::class.java)
                .block(Duration.ofMillis(10000))
    }
}
