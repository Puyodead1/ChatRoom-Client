package optic_fusion1.client.events;

import com.google.protobuf.InvalidProtocolBufferException;
import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.client.ClientChannelHandlerContext;
import optic_fusion1.common.protos.ErrorPacket;
import optic_fusion1.common.protos.Packet;

public record ErrorEvent(ClientChannelHandlerContext clientChannelHandlerContext, Packet packet) implements IEvent {
    public ErrorPacket getErrorData() throws InvalidProtocolBufferException {
        final byte[] packetData = packet.getData().toByteArray();
        return ErrorPacket.parseFrom(packetData);
    }
}
