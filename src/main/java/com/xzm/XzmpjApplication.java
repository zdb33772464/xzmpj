package com.xzm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class XzmpjApplication {
    public static void main(String[] args){
        SpringApplication.run(XzmpjApplication.class,args);
    }
}
