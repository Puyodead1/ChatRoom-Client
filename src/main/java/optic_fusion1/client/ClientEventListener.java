package optic_fusion1.client;

import com.google.protobuf.ByteString;
import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.EventTarget;
import optic_fusion1.client.events.ClientReadyEvent;
import optic_fusion1.client.events.ErrorEvent;
import optic_fusion1.client.events.HandshakeResponseEvent;
import optic_fusion1.client.events.MessageReceivedEvent;
import optic_fusion1.common.protos.HandshakeRequest;
import optic_fusion1.common.protos.HandshakeResponse;
import optic_fusion1.common.protos.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

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
        HandshakeRequest.Builder handshakeReq = HandshakeRequest.newBuilder();
        handshakeReq.setProtocolVersion(this.client.PROTOCOL_VERSION);
        handshakeReq.setRsaPublicKey(ByteString.copyFrom(this.client.rsaKeyPair.getPublic().getEncoded()));

        // Create the packet
        Packet.Builder packet = Packet.newBuilder();
        packet.setPacketType(Packet.Type.HANDSHAKE_REQUEST);
        packet.setHandshakeRequestData(handshakeReq);

        event.clientChannelHandlerContext().sendPacket(packet.build());
    }

    @EventTarget
    public void onMessageReceived(final MessageReceivedEvent event) {
        LOGGER.info("[MessageReceived] Received a message");
        final Packet.Type packetType = event.packet().getPacketType();
        switch(packetType) {
            case HANDSHAKE_RESPONSE -> EventManager.call(new HandshakeResponseEvent(event.clientChannelHandlerContext(), event.packet()));
            case ERROR -> EventManager.call(new ErrorEvent(event.clientChannelHandlerContext(), event.packet()));
            default -> LOGGER.warn(String.format("[Message Receive] Received an unknown packet type: %s", event.packet()));
        }
    }

    @EventTarget
    public void onError(final ErrorEvent event) {
        LOGGER.error(String.format("[Error] %s: %s", event.getErrorData().getErrorType().name(), event.getErrorData().getDescription()));
    }

    @EventTarget
    public void onHandshakeResponse(final HandshakeResponseEvent event) {
        final HandshakeResponse packetData = event.getHandshakeResponseData();
        try {
            final String sessionId = packetData.getSessionId();
            final PublicKey serverPublicKey = getPublicKeyFromBytes(packetData.getRsaPublicKey().toByteArray());
            final byte[] hmacKey = packetData.getHmacKey().toByteArray();

            LOGGER.info(String.format("[HandshakeResponse] Session ID: %s", sessionId));
        } catch(InvalidKeySpecException | NoSuchAlgorithmException ex) {
            LOGGER.error(String.format("[HandshakeResponse] Error loading server public key: %s", ex.getLocalizedMessage()));
        }
    }
}
