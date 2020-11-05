package com.thesong.socketrpc;

public class RpcConsumer {
    public static void main(String[] args) {
        SomeApi someApi = RpcFramework.refer(SomeApi.class, "localhost", 9999);
        Object result = someApi.hello("lusong");
        System.out.println(result);

    }
}
