package optic_fusion1.client.events;

import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.client.ClientChannelHandlerContext;

public record ClientReadyEvent(ClientChannelHandlerContext ctx) implements IEvent {
}
