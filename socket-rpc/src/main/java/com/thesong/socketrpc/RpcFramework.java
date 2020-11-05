package com.thesong.socketrpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author thesong
 * @Date 2020/11/5 17:45
 * @Version 1.0
 * @Describe
 */
public class RpcFramework {

    // 暴露方法

    public static void export(Object service, int port) throws Exception {
        if (service == null) {
            throw new IllegalArgumentException("service instance is null!");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("port " + port + "is invalid");
        }

        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket accept = serverSocket.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ObjectInputStream objectInputStream = null;
                    ObjectOutputStream objectOutputStream = null;
                    try {
                        objectInputStream = new ObjectInputStream(accept.getInputStream());
                        String s = objectInputStream.readUTF();
                        Class<?>[] parameterTypes = (Class<?>[]) objectInputStream.readObject();
                        Object[] argv = (Object[]) objectInputStream.readObject();
                        objectOutputStream = new ObjectOutputStream(accept.getOutputStream());
                        Object result = service.getClass().getMethod(s, parameterTypes).invoke(service, argv);
                        objectOutputStream.writeObject(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            objectOutputStream.close();
                            objectInputStream.close();
                            accept.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    }

    // 调用方法
    public static <T> T refer(Class<T> interfaceClass, String host, Integer port) {
        if (interfaceClass == null) {
            throw new IllegalArgumentException("interfaceClass is null!");
        }
        if (host == null) {
            throw new IllegalArgumentException("host " + host + "is invalid");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("port " + port + "is invalid");
        }

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Socket socket = new Socket(host, port);
                        ObjectInputStream objectInputStream = null;
                        ObjectOutputStream objectOutputStream = null;
                        try {
                            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            objectOutputStream.writeUTF(method.getName());
                            objectOutputStream.writeObject(method.getParameterTypes());
                            objectOutputStream.writeObject(args);
                            objectInputStream = new ObjectInputStream(socket.getInputStream());
                            Object result = objectInputStream.readObject();
                            return result;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            objectInputStream.close();
                            objectOutputStream.close();
                            socket.close();
                        }
                        return null;
                    }
                }
        );
    }
}
