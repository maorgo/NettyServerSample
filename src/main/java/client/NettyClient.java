package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient implements Runnable {
    private final ClientHandler clientHandler = new ClientHandler();
    private boolean isRunning = false;
    private String ip;
    private int port;

    public synchronized void start(String ip, int port) throws InterruptedException {
        if (!isRunning) {
            this.ip = ip;
            this.port = port;
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(this);
            isRunning = true;
        }
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(ip, port))
                    .handler(new ClientInitializer());

            // Start the client.
            ChannelFuture f = bootstrap.connect().sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void sendMessage(String msg) {
        clientHandler.sendMessage(msg);
    }

    public boolean isConnectionActive() {
        return clientHandler.isConnectionActive();
    }

    public static void main(String[] args) {
        try {
            NettyClient client = new NettyClient();
            client.start("localhost", 5000);
            while (!client.isConnectionActive()) {
                System.out.println("Waiting for the connection to be successful");
                Thread.sleep(500);
            }

            client.sendMessage("This shit works!!!");
        } catch (InterruptedException ie) {
            System.out.println(ie);
        }
    }
}
