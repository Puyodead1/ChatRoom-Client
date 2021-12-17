package optic_fusion1.client;

import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.EventTarget;
import optic_fusion1.client.events.ClientReadyEvent;
import optic_fusion1.client.events.ErrorEvent;
import optic_fusion1.client.events.MessageReceivedEvent;
import optic_fusion1.common.protos.HandshakePacket;
import optic_fusion1.common.protos.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientEventListener {
    private final Logger LOGGER = LogManager.getLogger(ClientEventListener.class);

    private final Client client;

    public ClientEventListener(final Client client) {
        this.client = client;
    }

    @EventTarget
    public void onClientReady(final ClientReadyEvent event) {
        LOGGER.info("[ClientReady] Client Ready");

        Packet.Builder packet = Packet.newBuilder();
        HandshakePacket.Builder handshake = HandshakePacket.newBuilder();
        handshake.setProtocolVersion(this.client.PROTOCOL_VERSION);

        packet.setPacketType(Packet.Type.HANDSHAKE);
        packet.setHandshakeData(handshake);

        event.ctx().sendPacket(packet.build());
    }

    @EventTarget
    public void onMessageReceived(final MessageReceivedEvent event) {
        LOGGER.info("[MessageReceived] Received a message");
        final Packet.Type packetType = event.packet().getPacketType();
        switch(packetType) {
            case ERROR -> EventManager.call(new ErrorEvent(event.ctx(), event.packet()));
            default -> LOGGER.warn(String.format("[Message Receive] Received an unknown packet type: %s", event.packet()));
        }
    }

    @EventTarget
    public void onError(final ErrorEvent event) {
        LOGGER.error(String.format("[Error] %s: %s", event.getErrorData().getErrorType().name(), event.getErrorData().getDescription()));
    }
}
