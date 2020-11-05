package rpc;

import com.thesong.rpc.dto.Invocation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.InvalidClassException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author thesong
 * @Date 2020/11/5 16:01
 * @Version 1.0
 * @Describe
 */
public class RPCServerHandler  extends SimpleChannelInboundHandler<Invocation> {

    private Map<String, Object> registerMap = new HashMap<>();

    public RPCServerHandler(Map<String, Object> registerMap){
        this.registerMap = registerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Invocation invocation) throws Exception {
        Object result = "没有指定的提供者方法";
        if (registerMap.containsKey(invocation.getClassName())) {
            Object provider = registerMap.get(invocation.getClassName());
            result = provider.getClass().getMethod(invocation.getMethodName(), invocation.getParamTypes())
                    .invoke(provider ,invocation.getParaValues());
        }
        channelHandlerContext.writeAndFlush(result);
        channelHandlerContext.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
