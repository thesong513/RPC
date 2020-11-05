package com.thesong.rpc.comsumer;

import com.thesong.rpc.dto.Invocation;
import com.thesong.rpc.service.SomeService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.InvalidPropertiesFormatException;

/**
 * @Author thesong
 * @Date 2020/11/5 13:52
 * @Version 1.0
 * @Describe
 */
public class RpcProxy {
    public static <T> T create(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        if (Object.class.equals(method.getDeclaredAnnotations())) {
                            return method.invoke(this, args);
                        }
                        return rpcInvoke(clazz, method, args);
                    }
                }
        );
    }

    private static Object rpcInvoke(Class<?> clazz , Method method, Object[] args) throws InterruptedException {
        RPCClientHandler rpcClientHandler = new RPCClientHandler();

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    // Nagel算法：尽量数据块大，关闭Nagel，有数据就发
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(rpcClientHandler);
                        }
                    });
            ChannelFuture future = bootstrap.connect("localhost", 8888).sync();
            //创建并初始化调用信息实例
            Invocation invocation = new Invocation();
            invocation.setClassName(clazz.getName());
            invocation.setMethodName(method.getName());
            invocation.setParamTypes(method.getParameterTypes());
            invocation.setParaValues(args);

            //将 invacation 发送给server
            future.channel().writeAndFlush(invocation).sync();
            future.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }

        return rpcClientHandler.getResult();
    }
}
