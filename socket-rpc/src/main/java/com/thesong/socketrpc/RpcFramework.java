package com.thesong.socketrpc;

import jdk.jfr.events.ThrowablesEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
        if (service==null) {
            throw new IllegalArgumentException("service instance == null");
        }
        if(port<1|| port>65535){
            throw new IllegalArgumentException("port "+port+"is invalid");
        }

        ServerSocket serverSocket = new ServerSocket();
        while(true){
            Socket accept = serverSocket.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ObjectInputStream objectInputStream =null;
                    ObjectOutputStream objectOutputStream = null;

                    try {
                        objectInputStream = new ObjectInputStream(accept.getInputStream());
                        String s = objectInputStream.readUTF();
                        Class<?>[] parameterTypes = (Class<?>[]) objectInputStream.readObject();
                        Object[] argv = (Object[]) objectInputStream.readObject();
                        Object result = service.getClass().getMethod(
                                s,parameterTypes
                        ).invoke(service, argv);
                        objectOutputStream.writeObject(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
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
    public <T> T refer(Class<T> interfaceClass, String host, Integer port){
        if (interfaceClass==null) {

        }
        if (host==null) {

        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("port "+port+"is invalid");
        }


    }




}
