package com.lr.test.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.coyote.http11.Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * Created by Administrator on 2017/4/9.
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String body = new String(bytes,"UTF-8");
        System.out.println("the time server receive order : " + body);
        String response = System.currentTimeMillis()+"";
        doWrite(ctx, response);
        super.channelRead(ctx, msg);


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        super.channelReadComplete(ctx);
    }

    private void doWrite(ChannelHandlerContext ctx,String response){
        ByteBuf resp = Unpooled.buffer();
//        Unpooled.copiedBuffer(response.getBytes());

        this.writeHeadLine(resp);
        this.writeHeader(resp, "Content-Length", response.getBytes().length + "");
        this.writeHeader(resp, "Content-Type", "text/html; charset=utf-8");
        resp.writeByte(13).writeByte(10)
                .writeBytes(response.getBytes());
        ctx.write(resp);
    }
    private void writeHeader(ByteBuf resp , String name,String value){
        resp.writeBytes(name.getBytes());
        resp.writeByte(58).writeByte(32).writeBytes(value.getBytes()).writeByte(13).writeByte(10);
    }
    private void writeHeadLine(ByteBuf resp){
        resp.writeBytes(Constants.HTTP_11_BYTES)
                .writeByte(32)
                .writeBytes(Constants._200_BYTES)
                .writeByte(32)
                .writeBytes("ok".getBytes())
                .writeByte(13).writeByte(10);
    }
}
