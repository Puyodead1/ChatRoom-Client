package optic_fusion1.client.events;

import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.client.ClientChannelHandlerContext;
import optic_fusion1.common.protos.Packet;

public record MessageReceivedEvent(ClientChannelHandlerContext ctx, Packet packet) implements IEvent {

}
