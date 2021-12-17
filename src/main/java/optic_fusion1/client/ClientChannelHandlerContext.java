package optic_fusion1.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import optic_fusion1.common.protos.Packet;

public class ClientChannelHandlerContext {

    private ChannelHandlerContext ctx;

    public ClientChannelHandlerContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelFuture sendPacket(Packet packet) {
        return this.ctx.writeAndFlush(packet);
    }
}
