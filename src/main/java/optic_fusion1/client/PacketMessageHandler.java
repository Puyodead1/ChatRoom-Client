package optic_fusion1.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import optic_fusion1.client.protos.Packet;
import optic_fusion1.client.protos.ProtocolVersion;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

public class PacketMessageHandler extends SimpleChannelInboundHandler<Packet> {
    private volatile Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Packet.Builder packet = Packet.newBuilder();

        packet.setPacketType(Packet.PacketType.HANDSHAKE);
        packet.setProtocolVersion(ProtocolVersion.VERSION_000);
        packet.setUseEncryption(true);

        ChannelFuture future = ctx.writeAndFlush(packet.build());
        future.addListener(FIRE_EXCEPTION_ON_FAILURE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet o) throws Exception {
        ReferenceCountUtil.release(o);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
