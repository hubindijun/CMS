# 第一阶段：项目脚手架 + RBAC 权限体系 实现计划

> **For agentic workers:** REQUIRED SUB-Skill: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 搭建多模块 Maven 项目骨架，完成 Spring Security + Redis Session 认证体系，实现用户-角色-权限三级 RBAC 权限管理，前端完成登录页 + 动态菜单布局 + 权限管理页面。

**Architecture:** 三模块 Maven 结构（cms-common / cms-admin / cms-api），cms-common 放公共基础类和数据库脚本，cms-admin 承载后台管理接口和 Spring Security 鉴权。前端 cms-admin-web 独立目录，Vue3 + Vite + Arco Design + TailwindCSS。权限采用菜单+按钮两级，后端 Spring Security 拦截，前端根据权限动态渲染菜单和按钮。

**Tech Stack:** Java 17, Spring Boot 3.3.x, Spring Security, Spring Session Redis, MyBatis-Plus 3.5.x, MySQL 8.0, Redis 6.x, Vue 3, Vite 5, Arco Design Vue, TailwindCSS 3, Pinia, Vue Router 4

**Spec:** docs/superpowers/specs/2026-08-24-cms-scaffolding-design.md

## Global Constraints

- Java 17+，Spring Boot 3.3.x
- MySQL 8.0（配置项默认 localhost:3306，密码待填）
- Redis 6.x（配置项默认 localhost:6379，密码待填）
- 数据库连接和 Redis 连接为预留配置，启动时不自动初始化数据库
- MyBatis-Plus 3.5.x（支持 Spring Boot 3.x）
- 前端 Vue 3 + Vite + Arco Design Vue + TailwindCSS
- 统一返回格式：`Result<T>` { code, message, data }
- 统一分页格式：`PageResult<T>` { list, total, pageNum, pageSize }
- 密码加密：BCrypt
- root 角色拥有所有权限，不可删除不可修改编码
- 代码风格：controller → service → mapper 三层架构，entity / dto / vo 实体分层

---

## Phase 1-A: 后端项目骨架

### Task 1: 创建父 pom 与多模块结构

**Files:**
- Create: `pom.xml`
- Create: `cms-common/pom.xml`
- Create: `cms-admin/pom.xml`
- Create: `cms-api/pom.xml`

**Interfaces:**
- Produces: 多模块 Maven 项目结构，cms-admin 依赖 cms-common，cms-api 依赖 cms-common

- [ ] **Step 1: 创建父 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.cms</groupId>
    <artifactId>cms</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    <name>cms</name>
    <description>CMS General Backend Scaffolding</description>

    <modules>
        <module>cms-common</module>
        <module>cms-admin</module>
        <module>cms-api</module>
    </modules>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.2</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <mybatis-plus.version>3.5.7</mybatis-plus.version>
        <knife4j.version>4.5.0</knife4j.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.baomidou</groupId>
                <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
                <version>${mybatis-plus.version}</version>
            </dependency>
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
                <version>${knife4j.version}</version>
            </dependency>
            <dependency>
                <groupId>com.cms</groupId>
                <artifactId>cms-common</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

- [ ] **Step 2: 创建 cms-common/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.cms</groupId>
        <artifactId>cms</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>cms-common</artifactId>
    <name>cms-common</name>

    <dependencies>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.32</version>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 3: 创建 cms-admin/pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.cms</groupId>
        <artifactId>cms</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>cms-admin</artifactId>
    <name>cms-admin</name>

    <dependencies>
        <dependency>
            <groupId>com.cms</groupId>
            <artifactId>cms-common</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>cms-admin</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>com.cms.admin.CmsAdminApplication</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 4: 创建 cms-api/pom.xml（预留，骨架即可）**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.cms</groupId>
        <artifactId>cms</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>cms-api</artifactId>
    <name>cms-api</name>

    <dependencies>
        <dependency>
            <groupId>com.cms</groupId>
            <artifactId>cms-common</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>cms-api</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 5: 验证 Maven 结构**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS（三个模块都编译通过）

- [ ] **Step 6: Commit**

```bash
git add pom.xml cms-common/pom.xml cms-admin/pom.xml cms-api/pom.xml
git commit -m "chore: create multi-module maven project structure"
```

---

### Task 2: 公共基础类（Result、PageResult、异常、常量、BaseEntity）

**Files:**
- Create: `cms-common/src/main/java/com/cms/common/base/Result.java`
- Create: `cms-common/src/main/java/com/cms/common/base/PageResult.java`
- Create: `cms-common/src/main/java/com/cms/common/base/BaseEntity.java`
- Create: `cms-common/src/main/java/com/cms/common/base/PageQuery.java`
- Create: `cms-common/src/main/java/com/cms/common/exception/GlobalExceptionHandler.java`
- Create: `cms-common/src/main/java/com/cms/common/exception/BusinessException.java`
- Create: `cms-common/src/main/java/com/cms/common/constant/CommonConstant.java`
- Create: `cms-common/src/main/java/com/cms/common/config/MybatisPlusConfig.java`

**Interfaces:**
- Produces: `Result.ok()/fail()`, `PageResult.of()`, `BaseEntity`（含 id、createTime、updateTime）, `BusinessException`, `GlobalExceptionHandler`

- [ ] **Step 1: 创建 Result.java**

```java
package com.cms.common.base;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        return r;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }
}
```

- [ ] **Step 2: 创建 PageResult.java**

```java
package com.cms.common.base;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private List<T> list;
    private long total;
    private long pageNum;
    private long pageSize;

    public static <T> PageResult<T> of(List<T> list, long total, long pageNum, long pageSize) {
        PageResult<T> r = new PageResult<>();
        r.setList(list);
        r.setTotal(total);
        r.setPageNum(pageNum);
        r.setPageSize(pageSize);
        return r;
    }
}
```

- [ ] **Step 3: 创建 BaseEntity.java**

```java
package com.cms.common.base;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}
```

- [ ] **Step 4: 创建 PageQuery.java**

```java
package com.cms.common.base;

import lombok.Data;
import java.io.Serializable;

@Data
public class PageQuery implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
```

- [ ] **Step 5: 创建 BusinessException.java**

```java
package com.cms.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
```

- [ ] **Step 6: 创建 GlobalExceptionHandler.java**

```java
package com.cms.common.exception;

import com.cms.common.base.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        return Result.fail(400, message);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数绑定失败";
        return Result.fail(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        return Result.fail(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(500, "系统异常，请联系管理员");
    }
}
```

- [ ] **Step 7: 创建 CommonConstant.java**

```java
package com.cms.common.constant;

public interface CommonConstant {
    String ROOT_ROLE_CODE = "root";
    String DEFAULT_PASSWORD = "123456";
    String CAPTCHA_PREFIX = "captcha:";
    long CAPTCHA_EXPIRE = 300;
    int LOGIN_MAX_RETRY = 5;
    String LOGIN_LOCK_PREFIX = "login:lock:";
    long LOGIN_LOCK_TIME = 600;
}
```

- [ ] **Step 8: 创建 MybatisPlusConfig.java**

```java
package com.cms.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

- [ ] **Step 9: 验证编译**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 10: Commit**

```bash
git add cms-common/src/main/java/com/cms/common/
git commit -m "feat: add common base classes (Result, PageResult, BaseEntity, exception handler)"
```

---

### Task 3: cms-admin 启动类 + 配置文件 + 数据库脚本

**Files:**
- Create: `cms-admin/src/main/java/com/cms/admin/CmsAdminApplication.java`
- Create: `cms-admin/src/main/java/com/cms/admin/config/MyMetaObjectHandler.java`
- Create: `cms-admin/src/main/resources/application.yml`
- Create: `cms-admin/src/main/resources/application-dev.yml`
- Create: `cms-common/src/main/resources/db/schema.sql`
- Create: `cms-common/src/main/resources/db/data.sql`

**Interfaces:**
- Consumes: BaseEntity（FieldFill 注解需要 MetaObjectHandler）
- Produces: 可启动的 Spring Boot 应用，数据库 DDL + 基础数据脚本

- [ ] **Step 1: 创建 CmsAdminApplication.java**

```java
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
```

- [ ] **Step 2: 创建 MyMetaObjectHandler.java**

```java
package com.cms.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUsername());
        this.strictInsertFill(metaObject, "updateBy", String.class, getCurrentUsername());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUsername());
    }

    private String getCurrentUsername() {
        return "system";
    }
}
```

> 注：getCurrentUsername 后续接入 Spring Security 后替换为真实登录用户名。

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  profiles:
    active: dev
  application:
    name: cms-admin

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.cms.admin.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

knife4j:
  enable: true
  openapi:
    title: CMS Admin API
    version: 1.0.0
    group:
      default:
        api-rule: package
        api-rule-resources:
          - com.cms.admin.controller
```

- [ ] **Step 4: 创建 application-dev.yml**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/cms?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0
  session:
    store-type: redis
    timeout: 3600
```

- [ ] **Step 5: 创建 schema.sql（系统表）**

```sql
-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    nickname VARCHAR(64) COMMENT '昵称',
    email VARCHAR(128) COMMENT '邮箱',
    phone VARCHAR(32) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL COMMENT '角色名称',
    code VARCHAR(64) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表（树形）
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    name VARCHAR(64) NOT NULL COMMENT '权限名称',
    type TINYINT NOT NULL COMMENT '类型 1目录 2菜单 3按钮',
    path VARCHAR(128) COMMENT '路由地址',
    component VARCHAR(255) COMMENT '组件路径',
    perms VARCHAR(128) COMMENT '权限标识',
    icon VARCHAR(64) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
```

- [ ] **Step 6: 创建 data.sql（基础数据）**

```sql
-- root 用户（密码: admin123，BCrypt 加密）
INSERT INTO sys_user (id, username, password, nickname, status) VALUES
(1, 'root', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 1);

-- root 角色
INSERT INTO sys_role (id, name, code, description, status) VALUES
(1, '超级管理员', 'root', '拥有所有权限', 1);

-- root 用户分配 root 角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 系统管理目录
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(1, 0, '系统管理', 1, '/system', NULL, NULL, 'icon-settings', 1);

-- 用户管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(2, 1, '用户管理', 2, '/system/user', 'system/user/index', NULL, 'icon-user', 1);

-- 用户管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(3, 2, '查询', 3, NULL, NULL, 'system:user:query', NULL, 1),
(4, 2, '新增', 3, NULL, NULL, 'system:user:add', NULL, 2),
(5, 2, '编辑', 3, NULL, NULL, 'system:user:edit', NULL, 3),
(6, 2, '删除', 3, NULL, NULL, 'system:user:delete', NULL, 4),
(7, 2, '分配角色', 3, NULL, NULL, 'system:user:role', NULL, 5),
(8, 2, '重置密码', 3, NULL, NULL, 'system:user:resetPwd', NULL, 6);

-- 角色管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(9, 1, '角色管理', 2, '/system/role', 'system/role/index', NULL, 'icon-user-group', 2);

-- 角色管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(10, 9, '查询', 3, NULL, NULL, 'system:role:query', NULL, 1),
(11, 9, '新增', 3, NULL, NULL, 'system:role:add', NULL, 2),
(12, 9, '编辑', 3, NULL, NULL, 'system:role:edit', NULL, 3),
(13, 9, '删除', 3, NULL, NULL, 'system:role:delete', NULL, 4),
(14, 9, '分配权限', 3, NULL, NULL, 'system:role:permission', NULL, 5);

-- 权限管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(15, 1, '权限管理', 2, '/system/permission', 'system/permission/index', NULL, 'icon-shield', 3);

-- 权限管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(16, 15, '查询', 3, NULL, NULL, 'system:permission:query', NULL, 1),
(17, 15, '新增', 3, NULL, NULL, 'system:permission:add', NULL, 2),
(18, 15, '编辑', 3, NULL, NULL, 'system:permission:edit', NULL, 3),
(19, 15, '删除', 3, NULL, NULL, 'system:permission:delete', NULL, 4);
```

- [ ] **Step 7: 验证编译和启动类**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add cms-admin/src/main/ cms-common/src/main/resources/db/
git commit -m "feat: cms-admin bootstrap + config + database schema and initial data"
```

---

## Phase 1-B: 后端 RBAC 权限体系

### Task 4: 实体类 + Mapper（用户、角色、权限 + 关联表）

**Files:**
- Create: `cms-admin/src/main/java/com/cms/admin/entity/SysUser.java`
- Create: `cms-admin/src/main/java/com/cms/admin/entity/SysRole.java`
- Create: `cms-admin/src/main/java/com/cms/admin/entity/SysPermission.java`
- Create: `cms-admin/src/main/java/com/cms/admin/entity/SysUserRole.java`
- Create: `cms-admin/src/main/java/com/cms/admin/entity/SysRolePermission.java`
- Create: `cms-admin/src/main/java/com/cms/admin/mapper/SysUserMapper.java`
- Create: `cms-admin/src/main/java/com/cms/admin/mapper/SysRoleMapper.java`
- Create: `cms-admin/src/main/java/com/cms/admin/mapper/SysPermissionMapper.java`
- Create: `cms-admin/src/main/java/com/cms/admin/mapper/SysUserRoleMapper.java`
- Create: `cms-admin/src/main/java/com/cms/admin/mapper/SysRolePermissionMapper.java`

**Interfaces:**
- Consumes: BaseEntity
- Produces: 5 个实体类 + 5 个 Mapper 接口

- [ ] **Step 1: 创建 SysUser.java**

```java
package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
}
```

- [ ] **Step 2: 创建 SysRole.java**

```java
package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {
    private String name;
    private String code;
    private String description;
    private Integer status;
}
```

- [ ] **Step 3: 创建 SysPermission.java**

```java
package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cms.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {
    private Long parentId;
    private String name;
    private Integer type;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer status;
}
```

- [ ] **Step 4: 创建 SysUserRole.java**

```java
package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long roleId;
}
```

- [ ] **Step 5: 创建 SysRolePermission.java**

```java
package com.cms.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("sys_role_permission")
public class SysRolePermission implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    private Long permissionId;
}
```

- [ ] **Step 6: 创建 5 个 Mapper 接口**

```java
// SysUserMapper.java
package com.cms.admin.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {}
```

```java
// SysRoleMapper.java
package com.cms.admin.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {}
```

```java
// SysPermissionMapper.java
package com.cms.admin.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    List<SysPermission> selectByUserId(Long userId);
    List<SysPermission> selectByRoleId(Long roleId);
}
```

```java
// SysUserRoleMapper.java
package com.cms.admin.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {}
```

```java
// SysRolePermissionMapper.java
package com.cms.admin.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.admin.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {}
```

- [ ] **Step 7: 创建 SysPermissionMapper.xml**

Create: `cms-admin/src/main/resources/mapper/SysPermissionMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.cms.admin.mapper.SysPermissionMapper">

    <select id="selectByUserId" resultType="com.cms.admin.entity.SysPermission">
        SELECT DISTINCT p.*
        FROM sys_permission p
        INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
        INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
          AND p.status = 1
          AND p.deleted = 0
        ORDER BY p.sort ASC
    </select>

    <select id="selectByRoleId" resultType="com.cms.admin.entity.SysPermission">
        SELECT p.*
        FROM sys_permission p
        INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
        WHERE rp.role_id = #{roleId}
          AND p.status = 1
          AND p.deleted = 0
        ORDER BY p.sort ASC
    </select>

</mapper>
```

- [ ] **Step 8: 验证编译**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 9: Commit**

```bash
git add cms-admin/src/main/java/com/cms/admin/entity/ cms-admin/src/main/java/com/cms/admin/mapper/ cms-admin/src/main/resources/mapper/
git commit -m "feat: add RBAC entities and mappers (user, role, permission)"
```

---

### Task 5: Spring Security 配置 + 登录认证 + 验证码

**Files:**
- Create: `cms-admin/src/main/java/com/cms/admin/security/CustomUserDetailsService.java`
- Create: `cms-admin/src/main/java/com/cms/admin/security/SecurityConfig.java`
- Create: `cms-admin/src/main/java/com/cms/admin/security/AuthenticationFailureHandler.java`
- Create: `cms-admin/src/main/java/com/cms/admin/security/AuthenticationSuccessHandler.java`
- Create: `cms-admin/src/main/java/com/cms/admin/security/LogoutSuccessHandler.java`
- Create: `cms-admin/src/main/java/com/cms/admin/security/DefaultAccessDeniedHandler.java`
- Create: `cms-admin/src/main/java/com/cms/admin/util/CaptchaUtil.java`
- Create: `cms-admin/src/main/java/com/cms/admin/controller/CaptchaController.java`
- Create: `cms-admin/src/main/java/com/cms/admin/controller/AuthController.java`
- Create: `cms-admin/src/main/java/com/cms/admin/security/permission/PermissionService.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/LoginDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/vo/UserInfoVO.java`

**Interfaces:**
- Consumes: SysUserMapper, SysRoleMapper, SysPermissionMapper
- Produces: `POST /api/login`, `POST /api/logout`, `GET /api/captcha`, `GET /api/auth/user-info`, Spring Security 配置

- [ ] **Step 1: 创建 LoginDTO.java**

```java
package com.cms.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String captcha;
    @NotBlank(message = "验证码key不能为空")
    private String captchaKey;
    private Boolean rememberMe;
}
```

- [ ] **Step 2: 创建 UserInfoVO.java**

```java
package com.cms.admin.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}
```

- [ ] **Step 3: 创建 CustomUserDetailsService.java**

```java
package com.cms.admin.security;

import com.cms.admin.entity.SysPermission;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysPermissionMapper;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.common.constant.CommonConstant;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已禁用");
        }

        List<Long> roleIds = userRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.cms.admin.entity.SysUserRole>()
                        .eq(com.cms.admin.entity.SysUserRole::getUserId, user.getId())
        ).stream().map(com.cms.admin.entity.SysUserRole::getRoleId).collect(Collectors.toList());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        boolean isRoot = roleMapper.selectBatchIds(roleIds).stream()
                .anyMatch(r -> CommonConstant.ROOT_ROLE_CODE.equals(r.getCode()));

        if (isRoot) {
            authorities.add(new SimpleGrantedAuthority("*:*:*"));
        } else {
            List<SysPermission> permissions = permissionMapper.selectByUserId(user.getId());
            for (SysPermission perm : permissions) {
                if (perm.getPerms() != null && !perm.getPerms().isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(perm.getPerms()));
                }
            }
        }

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
```

- [ ] **Step 4: 创建 PermissionService.java（自定义权限校验）**

```java
package com.cms.admin.security.permission;

import com.cms.common.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;

import java.util.Collection;

@Service("pms")
public class PermissionService {

    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(perm -> "*:*:*".equals(perm) || PatternMatchUtils.simpleMatch(permission, perm));
    }

    public boolean hasAnyPermission(String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
}
```

- [ ] **Step 5: 创建 CaptchaUtil.java**

```java
package com.cms.admin.util;

import cn.hutool.captcha.CaptchaUtil as HutoolCaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.cms.common.constant.CommonConstant;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaUtil {

    private final StringRedisTemplate redisTemplate;

    public CaptchaVO generate() {
        LineCaptcha captcha = HutoolCaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String key = IdUtil.fastSimpleUUID();
        String code = captcha.getCode();
        redisTemplate.opsForValue().set(CommonConstant.CAPTCHA_PREFIX + key, code,
                CommonConstant.CAPTCHA_EXPIRE, TimeUnit.SECONDS);
        CaptchaVO vo = new CaptchaVO();
        vo.setKey(key);
        vo.setImage(captcha.getImageBase64Data());
        return vo;
    }

    public boolean verify(String key, String code) {
        String cacheKey = CommonConstant.CAPTCHA_PREFIX + key;
        String cachedCode = redisTemplate.opsForValue().get(cacheKey);
        if (cachedCode == null) {
            return false;
        }
        redisTemplate.delete(cacheKey);
        return cachedCode.equalsIgnoreCase(code);
    }

    @lombok.Data
    public static class CaptchaVO {
        private String key;
        private String image;
    }
}
```

> 修正 import：`import cn.hutool.captcha.CaptchaUtil as HutoolCaptchaUtil;` 这行 Java 不支持 as 别名，直接改成静态导入或重命名变量。正确写法见下方：

```java
package com.cms.admin.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import com.cms.common.constant.CommonConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaUtil {

    private final StringRedisTemplate redisTemplate;

    public CaptchaVO generate() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String key = IdUtil.fastSimpleUUID();
        String code = captcha.getCode();
        redisTemplate.opsForValue().set(CommonConstant.CAPTCHA_PREFIX + key, code,
                CommonConstant.CAPTCHA_EXPIRE, TimeUnit.SECONDS);
        CaptchaVO vo = new CaptchaVO();
        vo.setKey(key);
        vo.setImage(captcha.getImageBase64Data());
        return vo;
    }

    public boolean verify(String key, String code) {
        String cacheKey = CommonConstant.CAPTCHA_PREFIX + key;
        String cachedCode = redisTemplate.opsForValue().get(cacheKey);
        if (cachedCode == null) {
            return false;
        }
        redisTemplate.delete(cacheKey);
        return cachedCode.equalsIgnoreCase(code);
    }

    @Data
    public static class CaptchaVO {
        private String key;
        private String image;
    }
}
```

- [ ] **Step 6: 创建 CaptchaController.java**

```java
package com.cms.admin.controller;

import com.cms.admin.util.CaptchaUtil;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaUtil captchaUtil;

    @GetMapping("/captcha")
    public Result<CaptchaUtil.CaptchaVO> captcha() {
        return Result.ok(captchaUtil.generate());
    }
}
```

- [ ] **Step 7: 创建认证成功/失败/拒绝处理器**

```java
// AuthenticationSuccessHandler.java
package com.cms.admin.security;

import com.cms.common.base.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.ok("登录成功")));
    }
}
```

```java
// AuthenticationFailureHandler.java
package com.cms.admin.security;

import com.cms.common.base.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String msg = "登录失败";
        if (exception instanceof BadCredentialsException) {
            msg = "用户名或密码错误";
        } else if (exception instanceof DisabledException) {
            msg = "账号已禁用";
        } else if (exception instanceof LockedException) {
            msg = "账号已锁定，请稍后再试";
        } else if (exception.getMessage() != null) {
            msg = exception.getMessage();
        }
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(msg)));
    }
}
```

```java
// LogoutSuccessHandler.java
package com.cms.admin.security;

import com.cms.common.base.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class LogoutSuccessHandler implements LogoutSuccessHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.ok("退出成功")));
    }
}
```

```java
// DefaultAccessDeniedHandler.java
package com.cms.admin.security;

import com.cms.common.base.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class DefaultAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(403, "权限不足")));
    }
}
```

- [ ] **Step 8: 创建 SecurityConfig.java**

```java
package com.cms.admin.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final DefaultAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/captcha",
                    "/api/auth/login",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/doc.html",
                    "/webjars/**",
                    "/swagger-resources/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
                })
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

- [ ] **Step 9: 创建 AuthController.java（获取用户信息）**

```java
package com.cms.admin.controller;

import com.cms.admin.entity.SysPermission;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysPermissionMapper;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.admin.vo.UserInfoVO;
import com.cms.common.base.Result;
import com.cms.common.constant.CommonConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    @GetMapping("/user-info")
    public Result<UserInfoVO> userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );

        List<Long> roleIds = userRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.cms.admin.entity.SysUserRole>()
                        .eq(com.cms.admin.entity.SysUserRole::getUserId, user.getId())
        ).stream().map(com.cms.admin.entity.SysUserRole::getRoleId).collect(Collectors.toList());

        List<String> roleCodes = roleMapper.selectBatchIds(roleIds).stream()
                .map(SysRole::getCode).collect(Collectors.toList());

        boolean isRoot = roleCodes.contains(CommonConstant.ROOT_ROLE_CODE);
        List<String> permissions;
        if (isRoot) {
            permissions = List.of("*:*:*");
        } else {
            permissions = permissionMapper.selectByUserId(user.getId()).stream()
                    .map(SysPermission::getPerms)
                    .filter(p -> p != null && !p.isEmpty())
                    .collect(Collectors.toList());
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRoles(roleCodes);
        vo.setPermissions(permissions);

        return Result.ok(vo);
    }
}
```

- [ ] **Step 10: 添加登录前验证码校验过滤器**

Create: `cms-admin/src/main/java/com/cms/admin/security/CaptchaFilter.java`

```java
package com.cms.admin.security;

import com.cms.admin.util.CaptchaUtil;
import com.cms.common.constant.CommonConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaFilter extends OncePerRequestFilter {

    private final CaptchaUtil captchaUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String username = request.getParameter("username");
            String lockKey = CommonConstant.LOGIN_LOCK_PREFIX + username;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
                throw new BadCredentialsException("账号已锁定，请10分钟后再试");
            }

            String captcha = request.getParameter("captcha");
            String captchaKey = request.getParameter("captchaKey");
            if (!captchaUtil.verify(captchaKey, captcha)) {
                incrementLoginFail(username);
                throw new BadCredentialsException("验证码错误");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void incrementLoginFail(String username) {
        String key = CommonConstant.LOGIN_LOCK_PREFIX + username + ":fail";
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count >= CommonConstant.LOGIN_MAX_RETRY) {
            redisTemplate.opsForValue().set(CommonConstant.LOGIN_LOCK_PREFIX + username, "locked",
                    CommonConstant.LOGIN_LOCK_TIME, TimeUnit.SECONDS);
            redisTemplate.delete(key);
        } else {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
    }
}
```

> 然后在 SecurityConfig 的 filterChain 里把 CaptchaFilter 加到 UsernamePasswordAuthenticationFilter 之前。在 filterChain 方法内添加：

```java
http.addFilterBefore(captchaFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
```

同时在 SecurityConfig 的构造依赖里加上 `private final CaptchaFilter captchaFilter;`

- [ ] **Step 11: 验证编译**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 12: Commit**

```bash
git add cms-admin/src/main/java/com/cms/admin/security/ cms-admin/src/main/java/com/cms/admin/util/ cms-admin/src/main/java/com/cms/admin/controller/AuthController.java cms-admin/src/main/java/com/cms/admin/controller/CaptchaController.java cms-admin/src/main/java/com/cms/admin/dto/ cms-admin/src/main/java/com/cms/admin/vo/
git commit -m "feat: Spring Security + Redis Session login with captcha"
```

---

### Task 6: 用户管理 CRUD + 分配角色

**Files:**
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/UserQueryDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/UserAddDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/UserUpdateDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/vo/system/UserVO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/service/SysUserService.java`
- Create: `cms-admin/src/main/java/com/cms/admin/service/impl/SysUserServiceImpl.java`
- Create: `cms-admin/src/main/java/com/cms/admin/controller/system/SysUserController.java`

**Interfaces:**
- Consumes: SysUserMapper, SysUserRoleMapper, SysRoleMapper
- Produces: 用户分页查询、新增、编辑、删除、状态切换、重置密码、分配角色

- [ ] **Step 1: 创建 DTO 和 VO**

```java
// UserQueryDTO.java
package com.cms.admin.dto.system;

import com.cms.common.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQuery {
    private String username;
    private String phone;
    private Integer status;
}
```

```java
// UserAddDTO.java
package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class UserAddDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private List<Long> roleIds;
}
```

```java
// UserUpdateDTO.java
package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class UserUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private List<Long> roleIds;
}
```

```java
// UserVO.java
package com.cms.admin.vo.system;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private List<Long> roleIds;
    private List<String> roleNames;
    private LocalDateTime createTime;
}
```

- [ ] **Step 2: 创建 SysUserService 接口**

```java
package com.cms.admin.service;

import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;

public interface SysUserService {
    PageResult<UserVO> page(UserQueryDTO dto);
    void add(UserAddDTO dto);
    void update(UserUpdateDTO dto);
    void delete(Long id);
    void toggleStatus(Long id, Integer status);
    void resetPassword(Long id, String newPassword);
    UserVO getById(Long id);
}
```

- [ ] **Step 3: 创建 SysUserServiceImpl.java**

```java
package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysUser;
import com.cms.admin.entity.SysUserRole;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.mapper.SysUserRoleMapper;
import com.cms.admin.service.SysUserService;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;
import com.cms.common.constant.CommonConstant;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> page(UserQueryDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getUsername())) {
            wrapper.like(SysUser::getUsername, dto.getUsername());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            wrapper.like(SysUser::getPhone, dto.getPhone());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(SysUser::getId);
        Page<SysUser> result = userMapper.selectPage(page, wrapper);

        List<UserVO> voList = result.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            List<Long> roleIds = getRoleIds(user.getId());
            vo.setRoleIds(roleIds);
            vo.setRoleNames(getRoleNames(roleIds));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    @Transactional
    public void add(UserAddDTO dto) {
        SysUser existing = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername())
        );
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            SysUser phoneExist = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, dto.getPhone())
            );
            if (phoneExist != null) {
                throw new BusinessException("手机号已存在");
            }
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (user.getStatus() == null) user.setStatus(1);
        userMapper.insert(user);

        saveUserRoles(user.getId(), dto.getRoleIds());
    }

    @Override
    @Transactional
    public void update(UserUpdateDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("root用户不可修改");
        }
        if (StringUtils.hasText(dto.getPhone())) {
            SysUser phoneExist = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getPhone, dto.getPhone())
                            .ne(SysUser::getId, dto.getId())
            );
            if (phoneExist != null) {
                throw new BusinessException("手机号已存在");
            }
        }

        SysUser update = new SysUser();
        BeanUtils.copyProperties(dto, update);
        userMapper.updateById(update);

        if (dto.getRoleIds() != null) {
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, dto.getId()));
            saveUserRoles(dto.getId(), dto.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) return;
        if (CommonConstant.ROOT_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("root用户不可删除");
        }
        userMapper.deleteById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    public void toggleStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(user.getUsername())) {
            throw new BusinessException("root用户不可禁用");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public UserVO getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) return null;
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        List<Long> roleIds = getRoleIds(id);
        vo.setRoleIds(roleIds);
        vo.setRoleNames(getRoleNames(roleIds));
        return vo;
    }

    private List<Long> getRoleIds(Long userId) {
        return userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    private List<String> getRoleNames(List<Long> roleIds) {
        if (roleIds.isEmpty()) return List.of();
        return roleMapper.selectBatchIds(roleIds).stream()
                .map(SysRole::getName).collect(Collectors.toList());
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return;
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }
}
```

- [ ] **Step 4: 创建 SysUserController.java**

```java
package com.cms.admin.controller.system;

import com.cms.admin.dto.system.UserAddDTO;
import com.cms.admin.dto.system.UserQueryDTO;
import com.cms.admin.dto.system.UserUpdateDTO;
import com.cms.admin.service.SysUserService;
import com.cms.admin.vo.system.UserVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import com.cms.common.constant.CommonConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:user:query')")
    public Result<PageResult<UserVO>> page(UserQueryDTO dto) {
        return Result.ok(userService.page(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:user:query')")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:user:add')")
    public Result<Void> add(@RequestBody @Valid UserAddDTO dto) {
        userService.add(dto);
        return Result.ok();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:user:edit')")
    public Result<Void> update(@RequestBody @Valid UserUpdateDTO dto) {
        userService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@pms.hasPermission('system:user:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.toggleStatus(id, status);
        return Result.ok();
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("@pms.hasPermission('system:user:resetPwd')")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String password) {
        userService.resetPassword(id, password);
        return Result.ok();
    }
}
```

- [ ] **Step 5: 验证编译**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add cms-admin/src/main/java/com/cms/admin/service/ cms-admin/src/main/java/com/cms/admin/controller/system/SysUserController.java cms-admin/src/main/java/com/cms/admin/dto/system/ cms-admin/src/main/java/com/cms/admin/vo/system/
git commit -m "feat: user management CRUD with role assignment"
```

---

### Task 7: 角色管理 CRUD + 分配权限

**Files:**
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/RoleQueryDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/RoleAddDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/RoleUpdateDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/vo/system/RoleVO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/service/SysRoleService.java`
- Create: `cms-admin/src/main/java/com/cms/admin/service/impl/SysRoleServiceImpl.java`
- Create: `cms-admin/src/main/java/com/cms/admin/controller/system/SysRoleController.java`

**Interfaces:**
- Consumes: SysRoleMapper, SysRolePermissionMapper, SysPermissionMapper
- Produces: 角色分页查询、新增、编辑、删除、状态切换、分配权限、获取角色权限ID列表

- [ ] **Step 1: 创建 DTO 和 VO**

```java
// RoleQueryDTO.java
package com.cms.admin.dto.system;

import com.cms.common.base.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQueryDTO extends PageQuery {
    private String name;
    private Integer status;
}
```

```java
// RoleAddDTO.java
package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class RoleAddDTO {
    @NotBlank(message = "角色名称不能为空")
    private String name;
    @NotBlank(message = "角色编码不能为空")
    private String code;
    private String description;
    private Integer status;
    private List<Long> permissionIds;
}
```

```java
// RoleUpdateDTO.java
package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class RoleUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;
    private String name;
    private String description;
    private Integer status;
    private List<Long> permissionIds;
}
```

```java
// RoleVO.java
package com.cms.admin.vo.system;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoleVO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
}
```

- [ ] **Step 2: 创建 SysRoleService 接口 + 实现**

```java
// SysRoleService.java
package com.cms.admin.service;

import com.cms.admin.dto.system.RoleAddDTO;
import com.cms.admin.dto.system.RoleQueryDTO;
import com.cms.admin.dto.system.RoleUpdateDTO;
import com.cms.admin.vo.system.RoleVO;
import com.cms.common.base.PageResult;
import java.util.List;

public interface SysRoleService {
    PageResult<RoleVO> page(RoleQueryDTO dto);
    List<RoleVO> listAll();
    void add(RoleAddDTO dto);
    void update(RoleUpdateDTO dto);
    void delete(Long id);
    void toggleStatus(Long id, Integer status);
    List<Long> getPermissionIds(Long id);
    void assignPermissions(Long id, List<Long> permissionIds);
}
```

```java
// SysRoleServiceImpl.java
package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.admin.dto.system.RoleAddDTO;
import com.cms.admin.dto.system.RoleQueryDTO;
import com.cms.admin.dto.system.RoleUpdateDTO;
import com.cms.admin.entity.SysRole;
import com.cms.admin.entity.SysRolePermission;
import com.cms.admin.mapper.SysRoleMapper;
import com.cms.admin.mapper.SysRolePermissionMapper;
import com.cms.admin.service.SysRoleService;
import com.cms.admin.vo.system.RoleVO;
import com.cms.common.base.PageResult;
import com.cms.common.constant.CommonConstant;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public PageResult<RoleVO> page(RoleQueryDTO dto) {
        Page<SysRole> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(SysRole::getName, dto.getName());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(SysRole::getId);
        Page<SysRole> result = roleMapper.selectPage(page, wrapper);

        List<RoleVO> voList = result.getRecords().stream().map(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    @Override
    public List<RoleVO> listAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1))
                .stream().map(role -> {
                    RoleVO vo = new RoleVO();
                    BeanUtils.copyProperties(role, vo);
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void add(RoleAddDTO dto) {
        SysRole existing = roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, dto.getCode())
        );
        if (existing != null) {
            throw new BusinessException("角色编码已存在");
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        if (role.getStatus() == null) role.setStatus(1);
        roleMapper.insert(role);

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            saveRolePermissions(role.getId(), dto.getPermissionIds());
        }
    }

    @Override
    @Transactional
    public void update(RoleUpdateDTO dto) {
        SysRole role = roleMapper.selectById(dto.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(role.getCode())) {
            throw new BusinessException("root角色不可修改");
        }
        SysRole update = new SysRole();
        BeanUtils.copyProperties(dto, update);
        roleMapper.updateById(update);

        if (dto.getPermissionIds() != null) {
            rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                    .eq(SysRolePermission::getRoleId, dto.getId()));
            saveRolePermissions(dto.getId(), dto.getPermissionIds());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) return;
        if (CommonConstant.ROOT_ROLE_CODE.equals(role.getCode())) {
            throw new BusinessException("root角色不可删除");
        }
        roleMapper.deleteById(id);
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, id));
    }

    @Override
    public void toggleStatus(Long id, Integer status) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(role.getCode())) {
            throw new BusinessException("root角色不可禁用");
        }
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    @Override
    public List<Long> getPermissionIds(Long id) {
        return rolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id)
        ).stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissions(Long id, List<Long> permissionIds) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        if (CommonConstant.ROOT_ROLE_CODE.equals(role.getCode())) {
            throw new BusinessException("root角色拥有所有权限，无需分配");
        }
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, id));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            saveRolePermissions(id, permissionIds);
        }
    }

    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        for (Long permId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permId);
            rolePermissionMapper.insert(rp);
        }
    }
}
```

- [ ] **Step 3: 创建 SysRoleController.java**

```java
package com.cms.admin.controller.system;

import com.cms.admin.dto.system.RoleAddDTO;
import com.cms.admin.dto.system.RoleQueryDTO;
import com.cms.admin.dto.system.RoleUpdateDTO;
import com.cms.admin.service.SysRoleService;
import com.cms.admin.vo.system.RoleVO;
import com.cms.common.base.PageResult;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('system:role:query')")
    public Result<PageResult<RoleVO>> page(RoleQueryDTO dto) {
        return Result.ok(roleService.page(dto));
    }

    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('system:role:query')")
    public Result<List<RoleVO>> list() {
        return Result.ok(roleService.listAll());
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:role:add')")
    public Result<Void> add(@RequestBody @Valid RoleAddDTO dto) {
        roleService.add(dto);
        return Result.ok();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    public Result<Void> update(@RequestBody @Valid RoleUpdateDTO dto) {
        roleService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("@pms.hasPermission('system:role:edit')")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleService.toggleStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("@pms.hasPermission('system:role:permission')")
    public Result<List<Long>> getPermissionIds(@PathVariable Long id) {
        return Result.ok(roleService.getPermissionIds(id));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("@pms.hasPermission('system:role:permission')")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.ok();
    }
}
```

- [ ] **Step 4: 验证编译**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add cms-admin/src/main/java/com/cms/admin/service/SysRoleService.java cms-admin/src/main/java/com/cms/admin/service/impl/SysRoleServiceImpl.java cms-admin/src/main/java/com/cms/admin/controller/system/SysRoleController.java cms-admin/src/main/java/com/cms/admin/dto/system/Role*.java cms-admin/src/main/java/com/cms/admin/vo/system/RoleVO.java
git commit -m "feat: role management CRUD with permission assignment"
```

---

### Task 8: 权限管理 CRUD（树形） + 菜单树接口

**Files:**
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/PermissionQueryDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/PermissionAddDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/PermissionUpdateDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/vo/system/PermissionVO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/service/SysPermissionService.java`
- Create: `cms-admin/src/main/java/com/cms/admin/service/impl/SysPermissionServiceImpl.java`
- Create: `cms-admin/src/main/java/com/cms/admin/controller/system/SysPermissionController.java`
- Create: `cms-admin/src/main/java/com/cms/admin/controller/system/SysMenuController.java`

**Interfaces:**
- Consumes: SysPermissionMapper
- Produces: 权限树形列表、新增、编辑、删除；动态菜单树（按当前用户权限返回）

- [ ] **Step 1: 创建 DTO 和 VO**

```java
// PermissionAddDTO.java
package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionAddDTO {
    private Long parentId;
    @NotBlank(message = "权限名称不能为空")
    private String name;
    @NotNull(message = "权限类型不能为空")
    private Integer type;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer status;
}
```

```java
// PermissionUpdateDTO.java
package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionUpdateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;
    private Long parentId;
    private String name;
    private Integer type;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer status;
}
```

```java
// PermissionVO.java
package com.cms.admin.vo.system;

import lombok.Data;
import java.util.List;

@Data
public class PermissionVO {
    private Long id;
    private Long parentId;
    private String name;
    private Integer type;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer status;
    private List<PermissionVO> children;
}
```

- [ ] **Step 2: 创建 SysPermissionService 接口 + 实现**

```java
// SysPermissionService.java
package com.cms.admin.service;

import com.cms.admin.dto.system.PermissionAddDTO;
import com.cms.admin.dto.system.PermissionUpdateDTO;
import com.cms.admin.vo.system.PermissionVO;
import java.util.List;

public interface SysPermissionService {
    List<PermissionVO> tree();
    List<PermissionVO> menuTreeByUserId(Long userId);
    void add(PermissionAddDTO dto);
    void update(PermissionUpdateDTO dto);
    void delete(Long id);
}
```

```java
// SysPermissionServiceImpl.java
package com.cms.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cms.admin.dto.system.PermissionAddDTO;
import com.cms.admin.dto.system.PermissionUpdateDTO;
import com.cms.admin.entity.SysPermission;
import com.cms.admin.mapper.SysPermissionMapper;
import com.cms.admin.service.SysPermissionService;
import com.cms.admin.vo.system.PermissionVO;
import com.cms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper permissionMapper;

    @Override
    public List<PermissionVO> tree() {
        List<SysPermission> all = permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSort)
        );
        return buildTree(all, 0L);
    }

    @Override
    public List<PermissionVO> menuTreeByUserId(Long userId) {
        List<SysPermission> all = permissionMapper.selectByUserId(userId);
        List<SysPermission> menus = all.stream()
                .filter(p -> p.getType() != null && p.getType() <= 2)
                .collect(Collectors.toList());
        return buildTree(menus, 0L);
    }

    @Override
    @Transactional
    public void add(PermissionAddDTO dto) {
        if (dto.getParentId() == null) dto.setParentId(0L);
        SysPermission perm = new SysPermission();
        BeanUtils.copyProperties(dto, perm);
        if (perm.getSort() == null) perm.setSort(0);
        if (perm.getStatus() == null) perm.setStatus(1);
        permissionMapper.insert(perm);
    }

    @Override
    @Transactional
    public void update(PermissionUpdateDTO dto) {
        SysPermission perm = permissionMapper.selectById(dto.getId());
        if (perm == null) {
            throw new BusinessException("权限不存在");
        }
        SysPermission update = new SysPermission();
        BeanUtils.copyProperties(dto, update);
        permissionMapper.updateById(update);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long count = permissionMapper.selectCount(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id)
        );
        if (count > 0) {
            throw new BusinessException("存在子级权限，不可删除");
        }
        permissionMapper.deleteById(id);
    }

    private List<PermissionVO> buildTree(List<SysPermission> list, Long parentId) {
        Map<Long, List<PermissionVO>> childrenMap = list.stream()
                .map(this::toVO)
                .collect(Collectors.groupingBy(PermissionVO::getParentId));
        return buildTreeRecursive(childrenMap, parentId);
    }

    private List<PermissionVO> buildTreeRecursive(Map<Long, List<PermissionVO>> childrenMap, Long parentId) {
        List<PermissionVO> children = childrenMap.get(parentId);
        if (children == null || children.isEmpty()) return new ArrayList<>();
        for (PermissionVO vo : children) {
            vo.setChildren(buildTreeRecursive(childrenMap, vo.getId()));
        }
        return children;
    }

    private PermissionVO toVO(SysPermission perm) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(perm, vo);
        return vo;
    }
}
```

- [ ] **Step 3: 创建 SysPermissionController.java**

```java
package com.cms.admin.controller.system;

import com.cms.admin.dto.system.PermissionAddDTO;
import com.cms.admin.dto.system.PermissionUpdateDTO;
import com.cms.admin.service.SysPermissionService;
import com.cms.admin.vo.system.PermissionVO;
import com.cms.common.base.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/system/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService permissionService;

    @GetMapping("/tree")
    @PreAuthorize("@pms.hasPermission('system:permission:query')")
    public Result<List<PermissionVO>> tree() {
        return Result.ok(permissionService.tree());
    }

    @PostMapping
    @PreAuthorize("@pms.hasPermission('system:permission:add')")
    public Result<Void> add(@RequestBody @Valid PermissionAddDTO dto) {
        permissionService.add(dto);
        return Result.ok();
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('system:permission:edit')")
    public Result<Void> update(@RequestBody @Valid PermissionUpdateDTO dto) {
        permissionService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('system:permission:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.ok();
    }
}
```

- [ ] **Step 4: 创建 SysMenuController.java（动态菜单接口）**

```java
package com.cms.admin.controller.system;

import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.admin.service.SysPermissionService;
import com.cms.admin.vo.system.PermissionVO;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysPermissionService permissionService;
    private final SysUserMapper userMapper;

    @GetMapping("/tree")
    public Result<List<PermissionVO>> menuTree() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );
        return Result.ok(permissionService.menuTreeByUserId(user.getId()));
    }
}
```

- [ ] **Step 5: 验证编译**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add cms-admin/src/main/java/com/cms/admin/service/SysPermissionService.java cms-admin/src/main/java/com/cms/admin/service/impl/SysPermissionServiceImpl.java cms-admin/src/main/java/com/cms/admin/controller/system/SysPermissionController.java cms-admin/src/main/java/com/cms/admin/controller/system/SysMenuController.java cms-admin/src/main/java/com/cms/admin/dto/system/Permission*.java cms-admin/src/main/java/com/cms/admin/vo/system/PermissionVO.java
git commit -m "feat: permission management (tree CRUD) + dynamic menu tree API"
```

---

## Phase 1-C: 前端项目骨架 + 权限管理页面

### Task 9: 前端项目初始化（Vue3 + Vite + Arco Design + TailwindCSS）

**Files:**
- Create: `cms-admin-web/package.json`
- Create: `cms-admin-web/vite.config.js`
- Create: `cms-admin-web/tailwind.config.js`
- Create: `cms-admin-web/postcss.config.js`
- Create: `cms-admin-web/index.html`
- Create: `cms-admin-web/src/main.js`
- Create: `cms-admin-web/src/App.vue`
- Create: `cms-admin-web/src/style.css`
- Create: `cms-admin-web/src/utils/request.js`
- Create: `cms-admin-web/src/stores/user.js`
- Create: `cms-admin-web/src/stores/permission.js`
- Create: `cms-admin-web/src/router/index.js`
- Create: `cms-admin-web/src/api/auth.js`
- Create: `.gitignore`（前端 node_modules）

**Interfaces:**
- Produces: 可运行的前端 dev 服务器，基础项目结构

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "cms-admin-web",
  "version": "1.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.7",
    "@arco-design/web-vue": "^2.56.0",
    "@arco-design/web-vue/es/icon": "^2.56.0",
    "axios": "^1.7.0",
    "nprogress": "^0.2.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.3.0",
    "tailwindcss": "^3.4.0",
    "postcss": "^8.4.0",
    "autoprefixer": "^10.4.0",
    "unplugin-vue-components": "^0.27.0",
    "unplugin-auto-import": "^0.17.0"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ArcoResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ArcoResolver()],
      imports: ['vue', 'vue-router', 'pinia']
    }),
    Components({
      resolvers: [ArcoResolver({ sideEffect: true })]
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 tailwind.config.js**

```js
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {},
  },
  plugins: [],
  corePlugins: {
    preflight: false,
  }
}
```

- [ ] **Step 4: 创建 postcss.config.js**

```js
export default {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
}
```

- [ ] **Step 5: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>CMS 管理后台</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 6: 创建 src/style.css**

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f6f7;
}
```

- [ ] **Step 7: 创建 src/main.js**

```js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ArcoVue from '@arco-design/web-vue'
import ArcoVueIcon from '@arco-design/web-vue/es/icon'
import '@arco-design/web-vue/dist/arco.css'
import App from './App.vue'
import router from './router'
import './style.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ArcoVue)
app.use(ArcoVueIcon)
app.mount('#app')
```

- [ ] **Step 8: 创建 src/App.vue**

```vue
<template>
  <router-view />
</template>

<script setup>
</script>
```

- [ ] **Step 9: 创建 src/utils/request.js**

```js
import axios from 'axios'
import { Message } from '@arco-design/web-vue'
import router from '@/router'

const request = axios.create({
  baseURL: '/',
  timeout: 30000,
  withCredentials: true
})

request.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => Promise.reject(error)
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else if (res.code === 401) {
      Message.error('登录已过期，请重新登录')
      router.push('/login')
      return Promise.reject(res)
    } else {
      Message.error(res.message || '请求失败')
      return Promise.reject(res)
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      Message.error('登录已过期，请重新登录')
      router.push('/login')
    } else if (error.response?.status === 403) {
      Message.error('权限不足')
    } else {
      Message.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 10: 创建 src/api/auth.js**

```js
import request from '@/utils/request'

export function login(data) {
  const form = new URLSearchParams()
  form.append('username', data.username)
  form.append('password', data.password)
  form.append('captcha', data.captcha)
  form.append('captchaKey', data.captchaKey)
  return request.post('/api/auth/login', form, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export function logout() {
  return request.post('/api/auth/logout')
}

export function getUserInfo() {
  return request.get('/api/auth/user-info')
}

export function getCaptcha() {
  return request.get('/api/auth/captcha')
}

export function getMenuTree() {
  return request.get('/api/menu/tree')
}
```

- [ ] **Step 11: 创建 src/stores/user.js**

```js
import { defineStore } from 'pinia'
import { login, logout, getUserInfo, getMenuTree } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    menuTree: [],
    permissions: []
  }),
  actions: {
    async login(formData) {
      await login(formData)
    },
    async fetchUserInfo() {
      const info = await getUserInfo()
      this.userInfo = info
      this.permissions = info.permissions || []
    },
    async fetchMenuTree() {
      const tree = await getMenuTree()
      this.menuTree = tree
    },
    async logout() {
      try {
        await logout()
      } catch (e) {}
      this.userInfo = null
      this.menuTree = []
      this.permissions = []
    }
  }
})
```

- [ ] **Step 12: 创建 src/router/index.js**

```js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  if (to.meta?.public) {
    next()
    return
  }

  const userStore = useUserStore()
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
      await userStore.fetchMenuTree()
    } catch (e) {
      next('/login')
      return
    }
  }
  next()
})

export default router
```

> 注：useUserStore 在 router 里使用需要注意 pinia 初始化时机，改为在 beforeEach 内部动态引入更稳妥。修正 router/index.js：

```js
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

let menuLoaded = false

router.beforeEach(async (to, from, next) => {
  if (to.meta?.public) {
    next()
    return
  }

  const userStore = useUserStore()
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
      await userStore.fetchMenuTree()
      menuLoaded = true
    } catch (e) {
      next(`/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})

export default router
```

- [ ] **Step 13: 创建 .gitignore**

```
node_modules
dist
dist-ssr
.vscode/*
!.vscode/extensions.json
.idea
.DS_Store
*.log
```

- [ ] **Step 14: 安装依赖并启动验证**

Run: 
```bash
cd /Users/hubin/IdeaProjects/cms/cms-admin-web
npm install
```
Expected: 依赖安装成功

- [ ] **Step 15: Commit**

```bash
git add cms-admin-web/ .gitignore
git commit -m "feat: initialize frontend project (Vue3 + Vite + Arco Design + TailwindCSS)"
```

---

### Task 10: 登录页 + 布局页（左侧菜单 + 顶部栏 + 动态路由）

**Files:**
- Create: `cms-admin-web/src/views/login/index.vue`
- Create: `cms-admin-web/src/layout/index.vue`
- Create: `cms-admin-web/src/layout/components/Sidebar.vue`
- Create: `cms-admin-web/src/layout/components/Header.vue`
- Create: `cms-admin-web/src/views/dashboard/index.vue`

**Interfaces:**
- Consumes: useUserStore（login, fetchUserInfo, fetchMenuTree, logout）
- Produces: 登录页（带图片验证码）、主布局、动态侧边栏菜单、个人中心入口

- [ ] **Step 1: 创建登录页 src/views/login/index.vue**

```vue
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1 class="text-2xl font-bold mb-2">CMS 管理系统</h1>
        <p class="text-gray-500 text-sm">欢迎登录</p>
      </div>
      <a-form :model="form" @submit="handleLogin" layout="vertical">
        <a-form-item field="username">
          <a-input
            v-model="form.username"
            placeholder="请输入用户名"
            size="large"
          >
            <template #prefix>
              <icon-user />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item field="password">
          <a-input-password
            v-model="form.password"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <icon-lock />
            </template>
          </a-input-password>
        </a-form-item>
        <a-form-item field="captcha">
          <div class="captcha-row">
            <a-input
              v-model="form.captcha"
              placeholder="验证码"
              size="large"
              class="captcha-input"
            >
              <template #prefix>
                <icon-safety />
              </template>
            </a-input>
            <img
              :src="captchaImage"
              class="captcha-img cursor-pointer"
              @click="refreshCaptcha"
              alt="验证码"
            />
          </div>
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            long
            size="large"
            :loading="loading"
            html-type="submit"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCaptcha } from '@/api/auth'
import { Message } from '@arco-design/web-vue'

const form = reactive({
  username: '',
  password: '',
  captcha: '',
  captchaKey: ''
})
const loading = ref(false)
const captchaImage = ref('')
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

async function refreshCaptcha() {
  const res = await getCaptcha()
  form.captchaKey = res.key
  captchaImage.value = res.image
}

async function handleLogin({ values }) {
  if (!form.username || !form.password || !form.captcha) {
    Message.warning('请填写完整信息')
    return
  }
  loading.value = true
  try {
    await userStore.login(form)
    Message.success('登录成功')
    await userStore.fetchUserInfo()
    await userStore.fetchMenuTree()
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}
.login-header {
  text-align: center;
  margin-bottom: 30px;
}
.captcha-row {
  display: flex;
  gap: 10px;
}
.captcha-input {
  flex: 1;
}
.captcha-img {
  width: 110px;
  height: 40px;
  border-radius: 4px;
  border: 1px solid #e5e6eb;
}
</style>
```

- [ ] **Step 2: 创建布局页 src/layout/index.vue**

```vue
<template>
  <a-layout class="layout-container">
    <Sidebar :menu-tree="userStore.menuTree" />
    <a-layout>
      <Header />
      <a-layout-content class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.main-content {
  margin: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 64px - 32px);
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
```

- [ ] **Step 3: 创建侧边栏 src/layout/components/Sidebar.vue**

```vue
<template>
  <a-layout-sider
    :width="220"
    :collapsible="true"
    :collapsed="collapsed"
    @collapse="(v) => collapsed = v"
    class="sidebar"
  >
    <div class="logo">
      <template v-if="!collapsed">CMS 管理系统</template>
      <template v-else>CMS</template>
    </div>
    <a-menu
      :default-selected-keys="[activeKey]"
      :open-keys="openKeys"
      @menu-item-click="handleClick"
    >
      <template v-for="item in menuTree" :key="item.id">
        <a-sub-menu v-if="item.children && item.children.length > 0" :key="item.id">
          <template #icon>
            <component :is="getIcon(item.icon)" />
          </template>
          <template #title>{{ item.name }}</template>
          <a-menu-item
            v-for="child in item.children"
            :key="child.id"
            @click="navigate(child)"
          >
            {{ child.name }}
          </a-menu-item>
        </a-sub-menu>
        <a-menu-item v-else :key="item.id" @click="navigate(item)">
          <template #icon>
            <component :is="getIcon(item.icon)" />
          </template>
          {{ item.name }}
        </a-menu-item>
      </template>
    </a-menu>
  </a-layout-sider>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  menuTree: {
    type: Array,
    default: () => []
  }
})

const collapsed = ref(false)
const route = useRoute()
const router = useRouter()
const openKeys = ref([])

const activeKey = computed(() => {
  return route.path
})

function navigate(item) {
  if (item.path) {
    router.push(item.path)
  }
}

function handleClick(key) {}

function getIcon(iconName) {
  if (!iconName) return 'icon-menu'
  const iconMap = {
    'icon-settings': 'icon-settings',
    'icon-user': 'icon-user',
    'icon-user-group': 'icon-user-group',
    'icon-shield': 'icon-safe',
    'icon-menu': 'icon-apps'
  }
  return iconMap[iconName] || 'icon-apps'
}

watch(() => props.menuTree, () => {
  if (props.menuTree.length > 0) {
    openKeys.value = props.menuTree.filter(m => m.children?.length > 0).map(m => String(m.id))
  }
}, { immediate: true })
</script>

<style scoped>
.sidebar {
  background: #fff;
  border-right: 1px solid #e5e6eb;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: #165dff;
  border-bottom: 1px solid #e5e6eb;
}
</style>
```

- [ ] **Step 4: 创建顶部栏 src/layout/components/Header.vue**

```vue
<template>
  <a-layout-header class="header">
    <div class="flex items-center justify-between h-full">
      <div class="text-lg font-medium">
        {{ currentTitle }}
      </div>
      <a-dropdown>
        <div class="user-info cursor-pointer flex items-center gap-2">
          <a-avatar size="small">
            <icon-user />
          </a-avatar>
          <span>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
        </div>
        <template #content>
          <a-dmenu>
            <a-dmenu-item @click="goProfile">
              <icon-user class="mr-2" /> 个人中心
            </a-dmenu-item>
            <a-dmenu-item @click="changePassword">
              <icon-lock class="mr-2" /> 修改密码
            </a-dmenu-item>
            <a-dmenu-divider />
            <a-dmenu-item @click="handleLogout">
              <icon-export class="mr-2" /> 退出登录
            </a-dmenu-item>
          </a-dmenu>
        </template>
      </a-dropdown>
    </div>
  </a-layout-header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Message, Modal } from '@arco-design/web-vue'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

const currentTitle = computed(() => route.meta?.title || '首页')

function goProfile() {
  Message.info('个人中心开发中')
}

function changePassword() {
  Message.info('修改密码开发中')
}

async function handleLogout() {
  Modal.confirm({
    title: '确认退出',
    content: '确定要退出登录吗？',
    onOk: async () => {
      await userStore.logout()
      Message.success('已退出登录')
      router.push('/login')
    }
  })
}
</script>

<style scoped>
.header {
  height: 60px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e5e6eb;
}
.user-info {
  padding: 0 8px;
}
</style>
```

- [ ] **Step 5: 创建仪表盘页面 src/views/dashboard/index.vue**

```vue
<template>
  <div class="dashboard">
    <a-space direction="vertical" :size="24" fill>
      <a-row :gutter="16">
        <a-col :span="6" v-for="stat in stats" :key="stat.title">
          <a-card>
            <a-statistic :title="stat.title" :value="stat.value" :style="{ color: stat.color }">
              <template #prefix>
                <component :is="stat.icon" :size="24" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
      </a-row>
      <a-card title="欢迎使用 CMS 管理系统">
        <p class="text-gray-600">这是一个通用后台管理脚手架，以电商业务为主线做功能演示。</p>
        <ul class="mt-4 text-gray-600 list-disc pl-6 space-y-1">
          <li>用户管理、角色管理、权限管理 — 完整的 RBAC 权限体系</li>
          <li>商品管理、订单管理、会员管理 — 电商业务示例模块</li>
          <li>字典管理、文件管理 — 通用工具模块</li>
        </ul>
      </a-card>
    </a-space>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const stats = ref([
  { title: '用户总数', value: 128, color: '#165dff', icon: 'icon-user' },
  { title: '商品总数', value: 256, color: '#00b42a', icon: 'icon-shopping-cart' },
  { title: '订单总数', value: 1024, color: '#ff7d00', icon: 'icon-file' },
  { title: '今日销售额', value: '¥8,888', color: '#f53f3f', icon: 'icon-wallet' }
])
</script>

<style scoped>
.dashboard {
  padding: 8px;
}
</style>
```

- [ ] **Step 6: 验证前端启动**

Run:
```bash
cd /Users/hubin/IdeaProjects/cms/cms-admin-web
npm run dev
```
Expected: Vite 启动成功，访问 http://localhost:5173 能看到登录页（即使后端没启动，页面应该能渲染）

- [ ] **Step 7: Commit**

```bash
git add cms-admin-web/src/views/login/ cms-admin-web/src/layout/ cms-admin-web/src/views/dashboard/
git commit -m "feat: login page + main layout with dynamic sidebar menu + dashboard"
```

---

### Task 11: 用户管理页面（列表 + 新增/编辑 + 分配角色）

**Files:**
- Create: `cms-admin-web/src/api/system/user.js`
- Create: `cms-admin-web/src/api/system/role.js`
- Create: `cms-admin-web/src/views/system/user/index.vue`
- Create: `cms-admin-web/src/utils/permission.js`

**Interfaces:**
- Consumes: 用户管理 API、角色列表 API
- Produces: 用户管理页面（查询、列表、新增、编辑、删除、分配角色、重置密码）

- [ ] **Step 1: 创建用户 API src/api/system/user.js**

```js
import request from '@/utils/request'

export function getUserPage(params) {
  return request.get('/api/system/user/page', { params })
}

export function getUser(id) {
  return request.get(`/api/system/user/${id}`)
}

export function addUser(data) {
  return request.post('/api/system/user', data)
}

export function updateUser(data) {
  return request.put('/api/system/user', data)
}

export function deleteUser(id) {
  return request.delete(`/api/system/user/${id}`)
}

export function toggleUserStatus(id, status) {
  return request.put(`/api/system/user/${id}/status`, null, { params: { status } })
}

export function resetPassword(id, password) {
  return request.put(`/api/system/user/${id}/reset-password`, null, { params: { password } })
}
```

- [ ] **Step 2: 创建角色 API src/api/system/role.js**

```js
import request from '@/utils/request'

export function getRolePage(params) {
  return request.get('/api/system/role/page', { params })
}

export function getRoleList() {
  return request.get('/api/system/role/list')
}

export function addRole(data) {
  return request.post('/api/system/role', data)
}

export function updateRole(data) {
  return request.put('/api/system/role', data)
}

export function deleteRole(id) {
  return request.delete(`/api/system/role/${id}`)
}

export function toggleRoleStatus(id, status) {
  return request.put(`/api/system/role/${id}/status`, null, { params: { status } })
}

export function getRolePermissions(id) {
  return request.get(`/api/system/role/${id}/permissions`)
}

export function assignRolePermissions(id, permissionIds) {
  return request.put(`/api/system/role/${id}/permissions`, permissionIds)
}
```

- [ ] **Step 3: 创建权限工具 src/utils/permission.js**

```js
import { useUserStore } from '@/stores/user'

export function hasPermission(perm) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []
  if (permissions.includes('*:*:*')) return true
  return permissions.includes(perm)
}
```

- [ ] **Step 4: 注册动态路由（在 router/index.js 中添加系统管理路由）**

> 在 router 的 children 数组里添加用户管理路由：

```js
// 在 layout 的 children 里补充
{
  path: '/system/user',
  name: 'SysUser',
  component: () => import('@/views/system/user/index.vue'),
  meta: { title: '用户管理' }
}
```

> 第一阶段先手动加这几个系统管理的路由，后续可以改成完全动态路由（从菜单树生成）。

- [ ] **Step 5: 创建用户管理页面 src/views/system/user/index.vue**

```vue
<template>
  <div class="p-4">
    <a-card>
      <!-- 查询条件 -->
      <a-form :model="queryForm" layout="inline" @submit="handleSearch">
        <a-form-item label="用户名" field="username">
          <a-input v-model="queryForm.username" placeholder="请输入用户名" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="手机号" field="phone">
          <a-input v-model="queryForm.phone" placeholder="请输入手机号" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="状态" field="status">
          <a-select v-model="queryForm.status" placeholder="全部" allow-clear style="width: 120px">
            <a-option :value="1">启用</a-option>
            <a-option :value="0">禁用</a-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit">
              <template #icon><icon-search /></template>
              查询
            </a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <!-- 操作栏 -->
      <div class="flex justify-between items-center mb-4 mt-4">
        <a-space>
          <a-button v-if="hasPerm('system:user:add')" type="primary" @click="handleAdd">
            <template #icon><icon-plus /></template>
            新增用户
          </a-button>
        </a-space>
      </div>

      <!-- 表格 -->
      <a-table
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @page-size-change="handleSizeChange"
        :bordered="false"
      >
        <template #columns>
          <a-table-column title="用户名" data-index="username" />
          <a-table-column title="昵称" data-index="nickname" />
          <a-table-column title="手机号" data-index="phone" />
          <a-table-column title="邮箱" data-index="email" />
          <a-table-column title="角色">
            <template #cell="{ record }">
              <a-tag v-for="name in record.roleNames" :key="name" color="blue">{{ name }}</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="状态" data-index="status">
            <template #cell="{ record }">
              <a-switch
                :checked="record.status === 1"
                :disabled="record.username === 'root'"
                @change="(v) => handleToggleStatus(record, v)"
              />
            </template>
          </a-table-column>
          <a-table-column title="创建时间" data-index="createTime" width="180" />
          <a-table-column title="操作" width="280" fixed="right">
            <template #cell="{ record }">
              <a-space size="small">
                <a-button v-if="hasPerm('system:user:edit')" type="text" size="small" @click="handleEdit(record)">编辑</a-button>
                <a-button v-if="hasPerm('system:user:role')" type="text" size="small" @click="handleAssignRole(record)">分配角色</a-button>
                <a-button v-if="hasPerm('system:user:resetPwd')" type="text" size="small" @click="handleResetPwd(record)">重置密码</a-button>
                <a-button v-if="hasPerm('system:user:delete')" type="text" status="danger" size="small" :disabled="record.username === 'root'" @click="handleDelete(record)">删除</a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
      width="600px"
    >
      <a-form :model="userForm" layout="vertical" ref="formRef">
        <a-form-item label="用户名" field="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model="userForm.username" :disabled="isEdit" />
        </a-form-item>
        <a-form-item v-if="!isEdit" label="密码" field="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model="userForm.password" />
        </a-form-item>
        <a-form-item label="昵称" field="nickname">
          <a-input v-model="userForm.nickname" />
        </a-form-item>
        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-item label="手机号" field="phone">
              <a-input v-model="userForm.phone" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="邮箱" field="email">
              <a-input v-model="userForm.email" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="状态" field="status">
          <a-radio-group v-model="userForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="角色" field="roleIds">
          <a-checkbox-group v-model="userForm.roleIds">
            <a-checkbox v-for="role in roleList" :key="role.id" :value="role.id">
              {{ role.name }}
            </a-checkbox>
          </a-checkbox-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 分配角色弹窗 -->
    <a-modal
      v-model:visible="roleModalVisible"
      title="分配角色"
      @ok="handleAssignRoleSubmit"
      width="400px"
    >
      <a-checkbox-group v-model="selectedRoleIds">
        <a-checkbox v-for="role in roleList" :key="role.id" :value="role.id">
          {{ role.name }}
        </a-checkbox>
      </a-checkbox-group>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { getUserPage, addUser, updateUser, deleteUser, toggleUserStatus, resetPassword } from '@/api/system/user'
import { getRoleList } from '@/api/system/role'
import { hasPermission } from '@/utils/permission'

const hasPerm = hasPermission

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const roleList = ref([])

const queryForm = reactive({
  username: '',
  phone: '',
  status: null
})

const pagination = reactive({
  total: 0,
  pageNum: 1,
  pageSize: 10,
  current: 1,
  pageSizeOptions: ['10', '20', '50', '100']
})

const modalVisible = ref(false)
const roleModalVisible = ref(false)
const isEdit = ref(false)
const currentUserId = ref(null)
const selectedRoleIds = ref([])

const userForm = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  status: 1,
  roleIds: []
})

const formRef = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const params = {
      ...queryForm,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    }
    const res = await getUserPage(params)
    tableData.value = res.list
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

async function fetchRoles() {
  roleList.value = await getRoleList()
}

function handleSearch() {
  pagination.current = 1
  fetchData()
}

function handleReset() {
  queryForm.username = ''
  queryForm.phone = ''
  queryForm.status = null
  handleSearch()
}

function handlePageChange(page) {
  pagination.current = page
  fetchData()
}

function handleSizeChange(size) {
  pagination.pageSize = size
  pagination.current = 1
  fetchData()
}

function handleAdd() {
  isEdit.value = false
  currentUserId.value = null
  Object.assign(userForm, {
    username: '',
    password: '',
    nickname: '',
    phone: '',
    email: '',
    status: 1,
    roleIds: []
  })
  modalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  currentUserId.value = record.id
  Object.assign(userForm, {
    username: record.username,
    nickname: record.nickname,
    phone: record.phone,
    email: record.email,
    status: record.status,
    roleIds: record.roleIds || []
  })
  modalVisible.value = true
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateUser({ id: currentUserId.value, ...userForm })
      Message.success('修改成功')
    } else {
      await addUser(userForm)
      Message.success('新增成功')
    }
    modalVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除用户「${record.username}」吗？`,
    status: 'warning',
    onOk: async () => {
      await deleteUser(record.id)
      Message.success('删除成功')
      fetchData()
    }
  })
}

async function handleToggleStatus(record, value) {
  await toggleUserStatus(record.id, value ? 1 : 0)
  Message.success('操作成功')
  fetchData()
}

function handleResetPwd(record) {
  Modal.confirm({
    title: '重置密码',
    content: `确定要将用户「${record.username}」的密码重置为 123456 吗？`,
    onOk: async () => {
      await resetPassword(record.id, '123456')
      Message.success('重置成功')
    }
  })
}

function handleAssignRole(record) {
  currentUserId.value = record.id
  selectedRoleIds.value = [...(record.roleIds || [])]
  roleModalVisible.value = true
}

async function handleAssignRoleSubmit() {
  await updateUser({ id: currentUserId.value, roleIds: selectedRoleIds.value })
  Message.success('分配成功')
  roleModalVisible.value = false
  fetchData()
}

onMounted(() => {
  fetchData()
  fetchRoles()
})
</script>
```

- [ ] **Step 6: 补充路由**

> 在 router/index.js 的 layout children 里添加：
```js
{
  path: '/system/user',
  name: 'SysUser',
  component: () => import('@/views/system/user/index.vue'),
  meta: { title: '用户管理' }
}
```

- [ ] **Step 7: 验证前端编译**

Run: `cd /Users/hubin/IdeaProjects/cms/cms-admin-web && npm run build`
Expected: 构建成功

- [ ] **Step 8: Commit**

```bash
git add cms-admin-web/src/api/system/ cms-admin-web/src/views/system/user/ cms-admin-web/src/utils/permission.js
git commit -m "feat: user management page with role assignment"
```

---

### Task 12: 角色管理页面 + 权限管理页面

**Files:**
- Create: `cms-admin-web/src/api/system/permission.js`
- Create: `cms-admin-web/src/views/system/role/index.vue`
- Create: `cms-admin-web/src/views/system/permission/index.vue`
- Update: `cms-admin-web/src/router/index.js`（添加路由）

**Interfaces:**
- Consumes: 角色 API、权限 API
- Produces: 角色管理页面（列表 + 分配权限树形选择）、权限管理页面（树形表格 + 增删改）

- [ ] **Step 1: 创建权限 API src/api/system/permission.js**

```js
import request from '@/utils/request'

export function getPermissionTree() {
  return request.get('/api/system/permission/tree')
}

export function addPermission(data) {
  return request.post('/api/system/permission', data)
}

export function updatePermission(data) {
  return request.put('/api/system/permission', data)
}

export function deletePermission(id) {
  return request.delete(`/api/system/permission/${id}`)
}
```

- [ ] **Step 2: 创建角色管理页面 src/views/system/role/index.vue**

```vue
<template>
  <div class="p-4">
    <a-card>
      <a-form :model="queryForm" layout="inline" @submit="handleSearch">
        <a-form-item label="角色名称" field="name">
          <a-input v-model="queryForm.name" placeholder="请输入角色名称" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="状态" field="status">
          <a-select v-model="queryForm.status" placeholder="全部" allow-clear style="width: 120px">
            <a-option :value="1">启用</a-option>
            <a-option :value="0">禁用</a-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit">查询</a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <div class="flex justify-between items-center mb-4 mt-4">
        <a-button v-if="hasPerm('system:role:add')" type="primary" @click="handleAdd">
          <template #icon><icon-plus /></template>
          新增角色
        </a-button>
      </div>

      <a-table
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @page-size-change="handleSizeChange"
      >
        <template #columns>
          <a-table-column title="角色名称" data-index="name" />
          <a-table-column title="角色编码" data-index="code" />
          <a-table-column title="描述" data-index="description" />
          <a-table-column title="状态" data-index="status">
            <template #cell="{ record }">
              <a-switch
                :checked="record.status === 1"
                :disabled="record.code === 'root'"
                @change="(v) => handleToggleStatus(record, v)"
              />
            </template>
          </a-table-column>
          <a-table-column title="创建时间" data-index="createTime" width="180" />
          <a-table-column title="操作" width="240" fixed="right">
            <template #cell="{ record }">
              <a-space size="small">
                <a-button v-if="hasPerm('system:role:edit')" type="text" size="small" @click="handleEdit(record)">编辑</a-button>
                <a-button v-if="hasPerm('system:role:permission')" type="text" size="small" @click="handleAssignPerm(record)">分配权限</a-button>
                <a-button v-if="hasPerm('system:role:delete')" type="text" status="danger" size="small" :disabled="record.code === 'root'" @click="handleDelete(record)">删除</a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑角色' : '新增角色'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
      width="500px"
    >
      <a-form :model="roleForm" layout="vertical">
        <a-form-item label="角色名称" :rules="[{ required: true, message: '请输入角色名称' }]">
          <a-input v-model="roleForm.name" />
        </a-form-item>
        <a-form-item label="角色编码" :rules="[{ required: true, message: '请输入角色编码' }]">
          <a-input v-model="roleForm.code" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model="roleForm.description" :rows="3" />
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model="roleForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 分配权限弹窗 -->
    <a-modal
      v-model:visible="permModalVisible"
      :title="`分配权限 - ${currentRoleName}`"
      @ok="handleAssignPermSubmit"
      :confirm-loading="submitLoading"
      width="500px"
    >
      <a-tree
        :data="permissionTree"
        :checked-keys="checkedPermIds"
        :default-expand-all="true"
        checkable
        @check="handlePermCheck"
      >
        <template #title="{ name, type }">
          <a-space size="small">
            <a-tag v-if="type === 1" color="blue">目录</a-tag>
            <a-tag v-else-if="type === 2" color="green">菜单</a-tag>
            <a-tag v-else color="orange">按钮</a-tag>
            {{ name }}
          </a-space>
        </template>
      </a-tree>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { getRolePage, addRole, updateRole, deleteRole, toggleRoleStatus, getRolePermissions, assignRolePermissions } from '@/api/system/role'
import { getPermissionTree } from '@/api/system/permission'
import { hasPermission } from '@/utils/permission'

const hasPerm = hasPermission

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const permissionTree = ref([])

const queryForm = reactive({ name: '', status: null })

const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
  pageSizeOptions: ['10', '20', '50', '100']
})

const modalVisible = ref(false)
const permModalVisible = ref(false)
const isEdit = ref(false)
const currentRoleId = ref(null)
const currentRoleName = ref('')
const checkedPermIds = ref([])
const checkedHalfKeys = ref([])

const roleForm = reactive({
  name: '',
  code: '',
  description: '',
  status: 1
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getRolePage({
      ...queryForm,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    })
    tableData.value = res.list
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

async function fetchPermissionTree() {
  const tree = await getPermissionTree()
  permissionTree.value = transformTree(tree)
}

function transformTree(list) {
  return list.map(item => ({
    key: item.id,
    title: item.name,
    name: item.name,
    type: item.type,
    children: item.children?.length ? transformTree(item.children) : undefined
  }))
}

function handleSearch() { pagination.current = 1; fetchData() }
function handleReset() { queryForm.name = ''; queryForm.status = null; handleSearch() }
function handlePageChange(p) { pagination.current = p; fetchData() }
function handleSizeChange(s) { pagination.pageSize = s; pagination.current = 1; fetchData() }

function handleAdd() {
  isEdit.value = false
  currentRoleId.value = null
  Object.assign(roleForm, { name: '', code: '', description: '', status: 1 })
  modalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  currentRoleId.value = record.id
  Object.assign(roleForm, { name: record.name, code: record.code, description: record.description, status: record.status })
  modalVisible.value = true
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateRole({ id: currentRoleId.value, ...roleForm })
      Message.success('修改成功')
    } else {
      await addRole(roleForm)
      Message.success('新增成功')
    }
    modalVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除角色「${record.name}」吗？`,
    status: 'warning',
    onOk: async () => {
      await deleteRole(record.id)
      Message.success('删除成功')
      fetchData()
    }
  })
}

async function handleToggleStatus(record, value) {
  await toggleRoleStatus(record.id, value ? 1 : 0)
  Message.success('操作成功')
  fetchData()
}

async function handleAssignPerm(record) {
  currentRoleId.value = record.id
  currentRoleName.value = record.name
  permModalVisible.value = true
  if (permissionTree.value.length === 0) {
    await fetchPermissionTree()
  }
  const permIds = await getRolePermissions(record.id)
  checkedPermIds.value = permIds
}

function handlePermCheck(keys) {
  checkedPermIds.value = keys
}

async function handleAssignPermSubmit() {
  submitLoading.value = true
  try {
    await assignRolePermissions(currentRoleId.value, checkedPermIds.value)
    Message.success('分配成功')
    permModalVisible.value = false
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>
```

- [ ] **Step 3: 创建权限管理页面 src/views/system/permission/index.vue**

```vue
<template>
  <div class="p-4">
    <a-card>
      <div class="flex justify-between items-center mb-4">
        <a-input
          v-model="searchKeyword"
          placeholder="搜索权限名称"
          allow-clear
          style="width: 240px"
        >
          <template #prefix><icon-search /></template>
        </a-input>
        <a-button v-if="hasPerm('system:permission:add')" type="primary" @click="handleAdd(0)">
          <template #icon><icon-plus /></template>
          新增权限
        </a-button>
      </div>

      <a-table
        :data="tableData"
        :loading="loading"
        :pagination="false"
        :row-key="record => record.id"
        :default-expand-all="true"
      >
        <template #columns>
          <a-table-column title="权限名称" data-index="name" :width="200" />
          <a-table-column title="类型" data-index="type" :width="100">
            <template #cell="{ record }">
              <a-tag v-if="record.type === 1" color="blue">目录</a-tag>
              <a-tag v-else-if="record.type === 2" color="green">菜单</a-tag>
              <a-tag v-else color="orange">按钮</a-tag>
            </template>
          </a-table-column>
          <a-table-column title="路由地址" data-index="path" />
          <a-table-column title="组件路径" data-index="component" />
          <a-table-column title="权限标识" data-index="perms" />
          <a-table-column title="排序" data-index="sort" :width="80" />
          <a-table-column title="状态" data-index="status" :width="100">
            <template #cell="{ record }">
              <a-tag :color="record.status === 1 ? 'green' : 'red'">
                {{ record.status === 1 ? '启用' : '禁用' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作" :width="200" fixed="right">
            <template #cell="{ record }">
              <a-space size="small">
                <a-button v-if="hasPerm('system:permission:add') && record.type !== 3" type="text" size="small" @click="handleAdd(record.id)">新增子级</a-button>
                <a-button v-if="hasPerm('system:permission:edit')" type="text" size="small" @click="handleEdit(record)">编辑</a-button>
                <a-button v-if="hasPerm('system:permission:delete')" type="text" status="danger" size="small" @click="handleDelete(record)">删除</a-button>
              </a-space>
            </template>
          </a-table-column>
        </template>
        <template #row-props="{ record }">
          { children: 'children', doNotCreateNestedRecord: true }
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="modalVisible"
      :title="isEdit ? '编辑权限' : '新增权限'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
      width="500px"
    >
      <a-form :model="permForm" layout="vertical">
        <a-form-item label="上级权限">
          <a-tree-select
            v-model="permForm.parentId"
            :data="treeSelectData"
            :default-expand-all="true"
            placeholder="顶级权限"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="权限名称" :rules="[{ required: true }]">
          <a-input v-model="permForm.name" />
        </a-form-item>
        <a-form-item label="类型" :rules="[{ required: true }]">
          <a-radio-group v-model="permForm.type">
            <a-radio :value="1">目录</a-radio>
            <a-radio :value="2">菜单</a-radio>
            <a-radio :value="3">按钮</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item v-if="permForm.type !== 3" label="路由地址">
          <a-input v-model="permForm.path" placeholder="如 /system/user" />
        </a-form-item>
        <a-form-item v-if="permForm.type === 2" label="组件路径">
          <a-input v-model="permForm.component" placeholder="如 system/user/index" />
        </a-form-item>
        <a-form-item v-if="permForm.type === 3" label="权限标识">
          <a-input v-model="permForm.perms" placeholder="如 system:user:add" />
        </a-form-item>
        <a-form-item v-if="permForm.type !== 3" label="图标">
          <a-input v-model="permForm.icon" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model="permForm.sort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model="permForm.status">
            <a-radio :value="1">启用</a-radio>
            <a-radio :value="0">禁用</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue'
import { Message, Modal } from '@arco-design/web-vue'
import { getPermissionTree, addPermission, updatePermission, deletePermission } from '@/api/system/permission'
import { hasPermission } from '@/utils/permission'

const hasPerm = hasPermission

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const searchKeyword = ref('')
const modalVisible = ref(false)
const isEdit = ref(false)
const currentPermId = ref(null)

const permForm = reactive({
  parentId: 0,
  name: '',
  type: 1,
  path: '',
  component: '',
  perms: '',
  icon: '',
  sort: 0,
  status: 1
})

const treeSelectData = computed(() => {
  return transformForSelect(tableData.value)
})

function transformForSelect(list) {
  return list
    .filter(item => item.type !== 3)
    .map(item => ({
      value: item.id,
      title: item.name,
      children: item.children?.length ? transformForSelect(item.children) : undefined
    }))
}

async function fetchData() {
  loading.value = true
  try {
    tableData.value = await getPermissionTree()
  } finally {
    loading.value = false
  }
}

function handleAdd(parentId) {
  isEdit.value = false
  currentPermId.value = null
  Object.assign(permForm, {
    parentId: parentId || 0,
    name: '',
    type: parentId === 0 ? 1 : 2,
    path: '',
    component: '',
    perms: '',
    icon: '',
    sort: 0,
    status: 1
  })
  modalVisible.value = true
}

function handleEdit(record) {
  isEdit.value = true
  currentPermId.value = record.id
  Object.assign(permForm, {
    parentId: record.parentId,
    name: record.name,
    type: record.type,
    path: record.path,
    component: record.component,
    perms: record.perms,
    icon: record.icon,
    sort: record.sort,
    status: record.status
  })
  modalVisible.value = true
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updatePermission({ id: currentPermId.value, ...permForm })
      Message.success('修改成功')
    } else {
      await addPermission(permForm)
      Message.success('新增成功')
    }
    modalVisible.value = false
    fetchData()
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(record) {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除权限「${record.name}」吗？`,
    status: 'warning',
    onOk: async () => {
      await deletePermission(record.id)
      Message.success('删除成功')
      fetchData()
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>
```

- [ ] **Step 4: 添加路由到 router/index.js**

```js
// layout children 新增：
{
  path: '/system/role',
  name: 'SysRole',
  component: () => import('@/views/system/role/index.vue'),
  meta: { title: '角色管理' }
},
{
  path: '/system/permission',
  name: 'SysPermission',
  component: () => import('@/views/system/permission/index.vue'),
  meta: { title: '权限管理' }
}
```

- [ ] **Step 5: 验证前端编译**

Run: `cd /Users/hubin/IdeaProjects/cms/cms-admin-web && npm run build`
Expected: 构建成功

- [ ] **Step 6: Commit**

```bash
git add cms-admin-web/src/api/system/permission.js cms-admin-web/src/views/system/role/ cms-admin-web/src/views/system/permission/
git commit -m "feat: role and permission management pages"
```

---

## Phase 1-D: 收尾

### Task 13: 个人中心 + 修改密码

**Files:**
- Create: `cms-admin/src/main/java/com/cms/admin/controller/system/ProfileController.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/ProfileUpdateDTO.java`
- Create: `cms-admin/src/main/java/com/cms/admin/dto/system/ChangePasswordDTO.java`
- Update: `cms-admin-web/src/layout/components/Header.vue`（跳转个人中心）
- Create: `cms-admin-web/src/views/profile/index.vue`

**Interfaces:**
- Produces: 个人信息查看修改、修改密码

- [ ] **Step 1: 创建 ProfileController.java**

```java
package com.cms.admin.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cms.admin.dto.system.ChangePasswordDTO;
import com.cms.admin.dto.system.ProfileUpdateDTO;
import com.cms.admin.entity.SysUser;
import com.cms.admin.mapper.SysUserMapper;
import com.cms.common.base.Result;
import com.cms.common.exception.BusinessException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Result<SysUser> profile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        user.setPassword(null);
        return Result.ok(user);
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid ProfileUpdateDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        userMapper.updateById(user);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody @Valid ChangePasswordDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
        return Result.ok();
    }
}
```

```java
// ProfileUpdateDTO.java
package com.cms.admin.dto.system;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
}
```

```java
// ChangePasswordDTO.java
package com.cms.admin.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "新密码至少6位")
    private String newPassword;
}
```

- [ ] **Step 2: 创建前端个人中心页面 + 对接 Header 跳转**

> 实现思路：创建 src/views/profile/index.vue 页面，展示个人信息表单和修改密码表单，Header 里的"个人中心"和"修改密码"跳转到该页面。

- [ ] **Step 3: 添加路由**

- [ ] **Step 4: 验证编译**

Run: `cd /Users/hubin/IdeaProjects/cms && mvn clean compile -q`
Expected: BUILD SUCCESS

Run: `cd /Users/hubin/IdeaProjects/cms/cms-admin-web && npm run build`
Expected: 构建成功

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: user profile page with change password"
```

---

### Task 14: 登录日志 + 字典管理（首期最小集）

> 这是第一阶段的收尾任务。登录日志做完整功能，字典管理做类型列表 + 数据列表。这是设计文档里确认有的功能，量不大，放进第一阶段做掉，后面阶段集中做电商模块。

**Files:**
- 登录日志（entity + mapper + service + controller + 前端页面）
- 字典管理（类型 + 数据，entity + mapper + service + controller + 前端页面）

- [ ] **Step 1-4: 后端实现（登录日志列表/删除，字典类型+字典数据CRUD）**
- [ ] **Step 5-7: 前端实现（两个列表页）**
- [ ] **Step 8: 更新数据库初始化脚本**
- [ ] **Step 9: 验证编译 + Commit**

> 注：这两个模块结构和用户管理类似，按相同模板实现即可，任务细节在实际执行时参照用户管理的代码模式。

---

**第一阶段总览：**

| 阶段 | 任务 | 产出 |
|------|------|------|
| 1-A | Task 1-3 | 后端项目骨架、公共基础类、启动类+配置+SQL |
| 1-B | Task 4-8 | RBAC 后端：实体+Mapper、登录认证+验证码、用户/角色/权限管理 API |
| 1-C | Task 9-12 | 前端：项目初始化、登录+布局、用户/角色/权限页面 |
| 1-D | Task 13-14 | 个人中心、登录日志、字典管理 |

**完成后可演示：** root 登录 → 查看动态菜单 → 用户管理（增删改查+分配角色）→ 角色管理（分配权限）→ 权限管理（树形维护）→ 验证按钮级权限（用普通账号登录看不到某些按钮）
