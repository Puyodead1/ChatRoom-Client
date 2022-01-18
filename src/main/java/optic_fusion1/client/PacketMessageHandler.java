package optic_fusion1.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import net.lenni0451.asmevents.EventManager;
import optic_fusion1.client.events.ClientReadyEvent;
import optic_fusion1.client.events.MessageReceivedEvent;
import optic_fusion1.common.protos.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketMessageHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Logger LOGGER = LogManager.getLogger(PacketMessageHandler.class);

    private volatile Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        EventManager.call(new ClientReadyEvent(new ClientChannelHandlerContext(ctx)));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        EventManager.call(new MessageReceivedEvent(new ClientChannelHandlerContext(ctx), packet));
        ReferenceCountUtil.release(packet);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Exception caught in package message handler", cause);
        ctx.close();
    }
}
