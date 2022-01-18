package optic_fusion1.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import optic_fusion1.common.protos.Packet;

public class ClientChannelHandlerContext {

    private ChannelHandlerContext channelHandlerContext;

    public ClientChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public ChannelFuture sendPacket(Packet packet) {
        return this.channelHandlerContext.writeAndFlush(packet);
    }
}
