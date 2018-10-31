package com.xzm.utils;


import java.util.UUID;

/**
 * UUID工具类
 */
public class UUIDUtil {
    /**
     * 获取生成的uuid
     * @return
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}