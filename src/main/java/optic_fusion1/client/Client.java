package optic_fusion1.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import optic_fusion1.common.protos.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    private final PacketMessageHandler messageHandler = new PacketMessageHandler();
    private boolean isRunning = false;
    private ExecutorService executor = null;

    public String host;
    public int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void start() {
        if(!isRunning) {
            executor = Executors.newFixedThreadPool(1);
            executor.execute(this);
            isRunning = true;
        }
    }

    public synchronized boolean stop() {
        LOGGER.info("Shutting down");
        boolean bReturn = true;
        if(isRunning) {
            if(executor != null) {
                executor.shutdown();
                try {
                    executor.shutdownNow();
                    if(executor.awaitTermination(calcTime(10, 0.66667), TimeUnit.SECONDS)) {
                        if(!executor.awaitTermination(calcTime(10, 0.33334), TimeUnit.SECONDS)) {
                            bReturn = false;
                        }
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                } finally {
                    executor = null;
                }
            }
            isRunning = false;
        }
        return bReturn;
    }

    private long calcTime(int nTime, double dValue) {
        return (long) ((double) nTime * dValue);
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();

                    p.addLast(new ProtobufVarint32FrameDecoder());
                    p.addLast(new ProtobufDecoder(Packet.getDefaultInstance()));
                    p.addLast(new ProtobufVarint32LengthFieldPrepender());
                    p.addLast(new ProtobufEncoder());
                    p.addLast(new PacketMessageHandler());
                }
            });

            b.connect(host, port).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.fatal(String.format("Failed to start ChatRoom client: %s", e.getLocalizedMessage()));
        } finally {
            group.shutdownGracefully();
        }
    }

    public ChannelFuture sendPacket(Packet packet) throws Exception {
        return messageHandler.sendPacket(packet);
    }
}
