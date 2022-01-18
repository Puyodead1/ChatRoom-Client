package optic_fusion1.client.events;

import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.client.ClientChannelHandlerContext;
import optic_fusion1.common.protos.ErrorPacket;
import optic_fusion1.common.protos.HandshakeResponse;
import optic_fusion1.common.protos.Packet;

public record HandshakeResponseEvent(ClientChannelHandlerContext clientChannelHandlerContext, Packet packet) implements IEvent {
    public HandshakeResponse getHandshakeResponseData() {
        return this.packet.getHandshakeResponseData();
    }
}