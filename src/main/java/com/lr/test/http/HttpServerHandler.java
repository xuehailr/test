package com.lr.test.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.omg.CORBA.BAD_CONTEXT;
import org.omg.CORBA.BAD_POLICY;
import org.omg.CORBA.BAD_QOS;

/**
 * Created by Administrator on 2017/4/10.
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        
    }
}
