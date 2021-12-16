package optic_fusion1.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.common.protos.ProtocolVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class PacketMessageHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Logger LOGGER = LogManager.getLogger(PacketMessageHandler.class);

    private volatile Channel channel;
    private ChannelHandlerContext ctx;

    public ChannelFuture sendPacket(Packet packet) throws Exception {
        if(ctx != null) {
            return ctx.writeAndFlush(packet);
        } else {
            throw new Exception("ChannelHandlerContext not initialized");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("Channel active");
        this.ctx = ctx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet o) throws Exception {
        LOGGER.debug("Channel read");
        ReferenceCountUtil.release(o);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("Channel registered");
        this.channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Exception caught in package message handler", cause);
        ctx.close();
    }
}
