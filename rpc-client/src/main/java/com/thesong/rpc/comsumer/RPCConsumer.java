package com.thesong.rpc.comsumer;

import com.thesong.rpc.service.SomeService;

/**
 * @Author thesong
 * @Date 2020/11/5 13:49
 * @Version 1.0
 * @Describe
 */
public class RPCConsumer {

    public static void main(String[] args) {
        SomeService service = RpcProxy.create(SomeService.class);
        long t1 = System.currentTimeMillis();
        System.out.println(service.hello("wode"));
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
        long t3 = System.currentTimeMillis();
        System.out.println(t3-t2);

        System.out.println(service.test(1));


    }
}
