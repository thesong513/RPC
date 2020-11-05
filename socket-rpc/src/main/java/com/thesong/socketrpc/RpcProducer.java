package com.thesong.socketrpc;

public class RpcProducer {
    public static void main(String[] args) throws Exception {
        SomeApiImpl someApi = new SomeApiImpl();
        RpcFramework.export(someApi,9999);
    }
}
