package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EchoChannelHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Echo channel handler first line");
        ByteBuf messageAsByteBuf = (ByteBuf) msg;
        String message = messageAsByteBuf.toString(StandardCharsets.ISO_8859_1);

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        String responseString = "Got msg at:  " + formatter.format(date) + " with size: " + message.length() +
                " from connection: " + ctx.name() + ": " + message;

        System.out.println(responseString);
        ByteBuf response = Unpooled.copiedBuffer(responseString, StandardCharsets.ISO_8859_1);
        ctx.writeAndFlush(response);
    }
}
