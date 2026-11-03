package com.cms.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CMS 对外接口服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.cms")
@MapperScan("com.cms.api.mapper")
public class CmsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsApiApplication.class, args);
    }
}
