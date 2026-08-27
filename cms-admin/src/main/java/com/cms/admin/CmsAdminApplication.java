package com.cms.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CMS 后台管理启动类
 */
@SpringBootApplication(scanBasePackages = "com.cms")
@MapperScan("com.cms.admin.mapper")
public class CmsAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsAdminApplication.class, args);
    }
}
