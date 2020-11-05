package com.thesong.rpc.comsumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author thesong
 * @Date 2020/11/5 16:20
 * @Version 1.0
 * @Describe
 */
public class RPCClientHandler extends SimpleChannelInboundHandler<Object> {

    private Object result;

    public Object getResult(){
        return result;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        result = o;
    }
}
