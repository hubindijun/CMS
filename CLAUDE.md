# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

CMS 通用后台管理脚手架，基于 Spring Boot 3.x + Vue 3 的电商后台管理系统。采用 RBAC 权限模型，以电商业务（商品、订单、会员）为主线做功能演示。

## 技术栈

**后端:** Java 17 + Spring Boot 3.3 + Spring Security + Spring Session Redis + MyBatis-Plus 3.5 + MySQL 8 + Redis 6 + Knife4j + Hutool

**前端:** Vue 3 + Vite 5 + Arco Design Vue（按需引入）+ TailwindCSS 3 + Pinia + Vue Router 4 + Axios

## 项目结构

```
cms/
├── cms-common/          # 公共模块：基础类、工具、常量、异常、SQL脚本
│   └── src/main/resources/db/   # schema.sql + data.sql
├── cms-admin/           # 后台管理模块（可执行 JAR，端口 8080）
├── cms-api/             # 对外接口模块（预留，空壳）
├── cms-admin-web/       # 前端管理后台（Vue3 + Vite，端口 5173）
└── docs/superpowers/    # 设计文档与实现计划
```

## 后端架构

### 代码分层
- **controller** → REST 接口层，统一返回 `Result<T>`
- **service** / **service/impl** → 业务逻辑层
- **mapper** → MyBatis-Plus Mapper 接口
- **entity** → 数据库实体，继承 `BaseEntity`（含 id, createTime, updateTime, createBy, updateBy, deleted）
- **dto** → 入参传输对象（QueryDTO 用于分页查询，AddDTO/UpdateDTO 用于新增/修改）
- **vo** → 出参视图对象

### 关键约定
- 统一返回格式：`Result<T>` { code: 200/500, message, data }
- 统一分页：`PageResult<T>` { list, total, pageNum, pageSize }
- 分页查询 DTO 继承 `PageQuery`（含 pageNum, pageSize）
- 逻辑删除：`deleted` 字段，MyBatis-Plus 自动处理（已删=1，未删=0）
- 主键自增：`IdType.AUTO`
- 权限标识规范：`模块:功能:操作`，如 `system:user:add`

### 权限体系（RBAC）
- 用户 → 角色 → 权限（多对多两级关联）
- 权限类型：1目录 / 2菜单 / 3按钮
- 后端接口鉴权：`@PreAuthorize("@pms.hasPermission('system:user:add')")`
- 超级管理员拥有 `*:*:*` 权限，匹配所有
- 会话存储：Spring Session + Redis（cookie 方式）
- 登录认证：表单登录 + 图片验证码 + 失败锁定

### 常用命令
```bash
# 后端构建
mvn clean package -DskipTests

# 后端运行（开发模式）
cd cms-admin && mvn spring-boot:run

# API 文档：http://localhost:8080/doc.html

# 数据库初始化
mysql -u root -p cms < cms-common/src/main/resources/db/schema.sql
mysql -u root -p cms < cms-common/src/main/resources/db/data.sql
```

## 前端架构

### 目录结构
```
cms-admin-web/src/
├── api/              # API 请求封装，按模块分文件（system/ 下为系统模块）
├── layout/           # 布局组件（Header + Sidebar）
├── views/            # 页面组件，按业务模块分目录
│   ├── system/       # 系统管理（用户/角色/权限/日志/字典）
│   ├── profile/      # 个人中心
│   ├── dashboard/    # 首页
│   └── login/        # 登录页
├── stores/           # Pinia 状态管理
├── router/           # 路由配置
└── utils/            # 工具函数（request, permission）
```

### 关键约定
- API 路径统一带 `/api` 前缀，Vite 代理到后端 `http://localhost:8080`
- 请求封装：`utils/request.js`，自动处理 401 跳转登录、统一错误提示
- 响应拦截器直接返回 `res.data`（已解包一层 Result）
- 认证方式：cookie-based session（`withCredentials: true`）
- 路由守卫：首次进入时拉取用户信息和菜单树
- Arco Design 组件按需自动引入（unplugin-vue-components）
- 自动导入 Vue / Vue Router / Pinia API（unplugin-auto-import）
- 路径别名：`@` → `src/`

### 按钮级权限
- `utils/permission.js` 提供权限检查工具
- 前端权限列表存在 Pinia user store 中（`permissions` 数组）
- 从后端 `/api/auth/userInfo` 获取，随用户信息一起返回

### 常用命令
```bash
cd cms-admin-web
npm install       # 安装依赖
npm run dev       # 开发模式（端口 5173）
npm run build     # 生产构建
npm run preview   # 预览构建产物
```

## 默认账号

- 用户名：`root`
- 密码：`admin123`
- 角色：超级管理员

## 开发阶段

当前处于第一阶段完成阶段，已实现 RBAC 权限体系（用户/角色/权限/登录认证/个人中心/登录日志/字典管理）。

待开发阶段：
- **第二阶段**：电商业务模块（商品管理、订单管理、会员管理）
- **第三阶段**：通用能力 + 对外接口（文件管理、系统配置、cms-api、MCP、代码生成器）
