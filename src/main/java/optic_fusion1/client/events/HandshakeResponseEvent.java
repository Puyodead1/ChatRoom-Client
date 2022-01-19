package optic_fusion1.client.events;

import com.google.protobuf.InvalidProtocolBufferException;
import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.client.ClientChannelHandlerContext;
import optic_fusion1.common.protos.HandshakeResponse;
import optic_fusion1.common.protos.Packet;

public record HandshakeResponseEvent(ClientChannelHandlerContext clientChannelHandlerContext, Packet packet) implements IEvent {
    public HandshakeResponse getResponseData() throws InvalidProtocolBufferException {
        final byte[] packetData = packet.getData().toByteArray();
        return HandshakeResponse.parseFrom(packetData);
    }
}
