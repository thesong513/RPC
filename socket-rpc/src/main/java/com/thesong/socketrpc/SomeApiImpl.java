package com.thesong.socketrpc;

/**
 * @Author thesong
 * @Date 2020/11/5 17:44
 * @Version 1.0
 * @Describe
 */
public class SomeApiImpl implements SomeApi{

    @Override
    public String hello(String name) {
        return "hello: "+ name;
    }
}
