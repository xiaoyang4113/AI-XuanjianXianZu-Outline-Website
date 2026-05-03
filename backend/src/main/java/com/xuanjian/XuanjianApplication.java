package com.xuanjian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xuanjian.mapper")
public class XuanjianApplication {
    public static void main(String[] args) {
        SpringApplication.run(XuanjianApplication.class, args);
    }
}