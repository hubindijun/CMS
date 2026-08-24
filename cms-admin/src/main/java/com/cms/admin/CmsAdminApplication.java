package com.cms.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.cms")
@MapperScan("com.cms.admin.mapper")
public class CmsAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsAdminApplication.class, args);
    }
}
