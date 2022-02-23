package server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.traffic.GlobalChannelTrafficShapingHandler;

public class EchoChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final long ONE_MEGABYTE = 1024 * 1024;
    private final EventLoopGroup workerGroup;

    public EchoChannelInitializer(EventLoopGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast(
                new GlobalChannelTrafficShapingHandler(workerGroup, ONE_MEGABYTE, 2, ONE_MEGABYTE, 1)
        );
        socketChannel.pipeline().addLast(new EchoChannelHandler());
    }
}
