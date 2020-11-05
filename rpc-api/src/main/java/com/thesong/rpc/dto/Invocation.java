package com.thesong.rpc.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author thesong
 * @Date 2020/11/5 15:50
 * @Version 1.0
 * @Describe
 */

@Data
public class Invocation  implements Serializable {
    //接口名
    private String className;
    //方法名
    private String methodName;
    //方法参数类型
    private Class<?>[] paramTypes;
    //方法参数数值
    private Object[] paraValues;

}
