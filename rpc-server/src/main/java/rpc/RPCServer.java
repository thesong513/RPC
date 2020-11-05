package rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import jdk.nashorn.internal.runtime.linker.Bootstrap;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author thesong
 * @Date 2020/11/5 12:45
 * @Version 1.0
 * @Describe
 */
public class RPCServer {

    //注册表
    private Map<String, Object> registerMap = new HashMap<>();
    private List<String> classCache = new ArrayList<>();

    public void publish(String basePackage) throws Exception {
        getProviderClass(basePackage);
        doRegister();

    }

    private void doRegister() throws Exception {
        if (classCache.size()==0) {
            return;
        }
        for (String className : classCache) {
            Class<?> clazz = Class.forName(className);
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length==1) {
                registerMap.put(interfaces[0].getName(), clazz.newInstance());
            }
        }

    }

    public void getProviderClass(String basePackage) {
        URL resource = this.getClass()
                .getClassLoader()
                .getResource(basePackage.replace(".","/"));
        if (resource==null) {
            return;
        }

        File dir = new File(resource.getFile());
        for(File file: dir.listFiles()){
            if (file.isDirectory()) {
                getProviderClass(basePackage+"."+file.getName());
            }else if(file.getName().endsWith(".class")){
                String simpleName = file.getName().replace(".class", "");
                classCache.add(basePackage+"."+simpleName);
            }
        }
    }

    public void start() throws InterruptedException {
        EventLoopGroup parentEventLoopGroup = new NioEventLoopGroup();
        EventLoopGroup childEventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentEventLoopGroup, childEventLoopGroup)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new RPCServerHandler(registerMap));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(8888).sync();
            System.out.println("服务器已经启动，端口号为：8888");
            future.channel().closeFuture().sync();
        }finally {
            parentEventLoopGroup.shutdownGracefully();
            childEventLoopGroup.shutdownGracefully();
        }


    }

}
