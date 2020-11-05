package rpc.service;

import com.thesong.rpc.service.SomeService;

import java.io.Serializable;

/**
 * @Author thesong
 * @Date 2020/11/5 16:39
 * @Version 1.0
 * @Describe
 */
public class SomeServiceImpl implements SomeService {

    @Override
    public String hello(String depart) {
        return "我的测试成功了！" + depart;
    }

    @Override
    public Integer test(Integer x) {
        return x;
    }
}
