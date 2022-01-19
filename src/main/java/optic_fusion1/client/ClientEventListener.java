package optic_fusion1.client;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.EventTarget;
import optic_fusion1.client.events.*;
import optic_fusion1.common.protos.AuthenticationRequestPacket;
import optic_fusion1.common.protos.ErrorPacket;
import optic_fusion1.common.protos.HandshakeRequest;
import optic_fusion1.common.protos.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import static optic_fusion1.common.ChatRoomUtils.makeErrorPacket;
import static optic_fusion1.common.RSAUtils.generateSignature;
import static optic_fusion1.common.RSAUtils.getPublicKeyFromBytes;

public class ClientEventListener {
    private final Logger LOGGER = LogManager.getLogger(ClientEventListener.class);

    private final Client client;

    public ClientEventListener(final Client client) {
        this.client = client;
    }

    @EventTarget
    public void onClientReady(final ClientReadyEvent event) {
        LOGGER.info("[ClientReady] Client Ready");

        // Create the handshake request data
        final HandshakeRequest.Builder handshakeReq = HandshakeRequest.newBuilder();
        handshakeReq.setProtocolVersion(this.client.PROTOCOL_VERSION);
        handshakeReq.setRsaPublicKey(ByteString.copyFrom(this.client.rsaKeyPair.getPublic().getEncoded()));

        // Create the packet
        final Packet.Builder packet = Packet.newBuilder();
        packet.setPacketType(Packet.Type.HANDSHAKE_REQUEST);
        packet.setData(handshakeReq.build().toByteString());

        event.clientChannelHandlerContext().sendPacket(packet.build());
    }

    @EventTarget
    public void onMessageReceived(final MessageReceivedEvent event) {
        LOGGER.info("[MessageReceived] Received a message");
        final Packet.Type packetType = event.packet().getPacketType();
        switch (packetType) {
            case HANDSHAKE_RESPONSE -> EventManager.call(new HandshakeResponseEvent(event.clientChannelHandlerContext(), event.packet()));
            case ERROR -> EventManager.call(new ErrorEvent(event.clientChannelHandlerContext(), event.packet()));
            default -> LOGGER.warn(String.format("[Message Receive] Received an unknown packet type: %s", event.packet()));
        }
    }

    @EventTarget
    public void onError(final ErrorEvent event) {
        try {
            LOGGER.error(String.format("[Error] %s: %s", event.getErrorData().getErrorType().name(), event.getErrorData().getDescription()));
        } catch (InvalidProtocolBufferException ex) {
            LOGGER.error(String.format("[Error] Received an error event, but failed to reassemble data! %s", ex.getLocalizedMessage()));
        }
    }

    @EventTarget
    public void onHandshakeResponse(final HandshakeResponseEvent event) {
        try {
            final String sessionId = event.getResponseData().getSessionId();
            final PublicKey serverPublicKey = getPublicKeyFromBytes(event.getResponseData().getRsaPublicKey().toByteArray());
            final byte[] hmacKey = event.getResponseData().getHmacKey().toByteArray();
            final boolean authRequired = event.getResponseData().getAuthenticationRequired();

            final Session session = new Session(sessionId, serverPublicKey, hmacKey, authRequired);
            this.client.setSession(session);

            LOGGER.info(String.format("[HandshakeResponse] Session ID: %s", sessionId));

            EventManager.call(new SessionReadyEvent(event.clientChannelHandlerContext()));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            LOGGER.error(String.format("[HandshakeResponse] Error loading server public key: %s", ex.getLocalizedMessage()));
        } catch (InvalidProtocolBufferException ex) {
            LOGGER.error(String.format("[HandshakeResponse] Exception caught: %s", ex.getLocalizedMessage()));

            final Packet packet = makeErrorPacket(ErrorPacket.Type.UNKNOWN, ex.getLocalizedMessage());

            event.clientChannelHandlerContext().sendPacket(packet);
        }
    }

    @EventTarget
    public void onSessionReady(final SessionReadyEvent event) {
        LOGGER.info("[SessionReady] Session Ready");

        if (this.client.getSession().isAuthRequired()) {
            // send authentication to the server
            try {
                final AuthenticationRequestPacket.Builder authPacket = AuthenticationRequestPacket.newBuilder();
                authPacket.setSessionId(this.client.getSession().getId().toString());
                authPacket.setUsername(this.client.username);
                authPacket.setPassword(this.client.password);

                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, this.client.getSession().getServerPublicKey());
                final byte[] encryptedAuthData = cipher.doFinal(authPacket.build().toByteArray());

                final byte[] signature = generateSignature(encryptedAuthData, this.client.rsaKeyPair.getPrivate());

                final Packet.Builder packet = Packet.newBuilder();
                packet.setPacketType(Packet.Type.AUTHENTICATION_REQUEST);
                packet.setData(ByteString.copyFrom(encryptedAuthData));
                packet.setSignature(ByteString.copyFrom(signature));

                event.clientChannelHandlerContext().sendPacket(packet.build());
            } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException ex) {
                LOGGER.error(String.format("[SessionReady] Exception caught while trying to create authentication request packet: %s", ex.getLocalizedMessage()));
            } catch (SignatureException | NoSuchProviderException ex) {
                LOGGER.error(String.format("[SessionReady] Exception caught while trying to generate signature: %s", ex.getLocalizedMessage()));
            }
        }
    }
}
