# CMS 通用后台管理脚手架

基于 Spring Boot 3.x + Vue 3 的通用后台管理系统脚手架，以电商业务为主线做功能演示。

## 技术栈

### 后端
- **Java 17+** + **Spring Boot 3.3.x**
- **Spring Security** — 认证授权
- **Token + Redis** — 无状态分布式认证（Bearer Token，2小时滑动过期）
- **MyBatis-Plus 3.5.x** — ORM + 代码生成
- **MySQL 8.0** — 主数据库
- **Redis 6.x** — Token 存储 + 权限缓存 + 验证码
- **Knife4j / Swagger UI** — API 文档
- **Hutool** — 工具类库

### 前端
- **Vue 3** + **Vite 5**
- **Arco Design Vue** — UI 组件库（按需引入）
- **TailwindCSS 3** — 样式工具
- **Pinia** — 状态管理
- **Vue Router 4** — 路由管理
- **Axios** — HTTP 请求

### 项目结构
```
cms/
├── cms-common/               # 公共模块（基础类、工具、常量、异常）
├── cms-admin/                # 后台管理模块（可执行 JAR）
├── cms-api/                  # 对外接口模块（预留，RESTful + MCP）
├── cms-admin-web/            # 前端管理后台（Vue3 + Vite）
└── docs/                     # 设计文档与实现计划
```

## 快速开始

### 环境准备
- JDK 17+
- Maven 3.9+
- MySQL 8.0+
- Redis 6.x+
- Node.js 20+

### 后端启动

**1. 初始化数据库**
```sql
-- 创建数据库
CREATE DATABASE cms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

执行建表脚本和初始数据：
```bash
# 建表
mysql -u root -p cms < cms-common/src/main/resources/db/schema.sql

# 初始数据（root用户、角色、权限、字典等）
mysql -u root -p cms < cms-common/src/main/resources/db/data.sql
```

**2. 修改配置**

编辑 `cms-admin/src/main/resources/application-dev.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cms?...
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

**3. 启动后端**
```bash
mvn clean package -DskipTests
java -jar cms-admin/target/cms-admin.jar
```

或开发模式：
```bash
cd cms-admin
mvn spring-boot:run
```

API 文档地址：http://localhost:8080/doc.html

### 前端启动

```bash
cd cms-admin-web
npm install
npm run dev
```

访问 http://localhost:5173

### 默认账号
- 用户名：`admin`
- 密码：`admin123`
- 角色：超级管理员（拥有所有权限）

## 功能模块

### ✅ 第一阶段：RBAC 权限体系（已完成）
- [x] 用户管理 — 增删改查、状态切换、重置密码、分配角色
- [x] 角色管理 — 增删改查、状态切换、分配权限（树形复选）
- [x] 权限管理 — 树形结构、目录/菜单/资源路径配置
- [x] 登录认证 — Token + Redis、图片验证码、失败锁定
- [x] 个人中心 — 基本信息修改、修改密码
- [x] 登录日志 — 查询、删除、清空
- [x] 字典管理 — 字典类型 + 字典数据
- [x] 动态菜单 — 按角色权限渲染左侧菜单和路由
- [x] 角色级权限 — @PreAuthorize("hasRole('xxx')") 接口拦截

### 📋 第二阶段：商品管理（待开发）
- [ ] 商品分类
  - [ ] 分类树 — 树形结构、无限级分类
  - [ ] 分类管理 — 新增/编辑/删除、排序、启用/禁用
  - [ ] 分类属性 — 关联规格参数
- [ ] 商品列表
  - [ ] 商品查询 — 关键字搜索、分类筛选、上下架状态、价格区间
  - [ ] 商品新增 — 基本信息、分类、主图、详情、规格、库存
  - [ ] 商品编辑 — 修改商品信息、批量上下架
  - [ ] 商品删除 — 逻辑删除、批量删除
  - [ ] 商品详情 — 完整商品信息展示
- [ ] 商品规格
  - [ ] 规格组管理 — 规格名称（如颜色、尺寸）
  - [ ] 规格值管理 — 规格值列表（如红/蓝/绿、S/M/L）
- [ ] 库存管理
  - [ ] SKU 列表 — 商品规格组合及对应库存
  - [ ] 库存调整 — 入库/出库、库存预警阈值设置
  - [ ] 库存预警 — 低于阈值的商品列表
  - [ ] 库存流水 — 出入库记录查询

### 📋 第二阶段：订单管理（待开发）
- [ ] 订单列表
  - [ ] 订单查询 — 订单号、会员、状态、时间范围筛选
  - [ ] 订单详情 — 订单基本信息、商品明细、收货地址、操作日志
  - [ ] 订单发货 — 填写物流单号、快递公司
  - [ ] 订单改价 — 修改商品金额、运费
  - [ ] 订单备注 — 商家备注、买家备注
  - [ ] 关闭订单 — 取消订单（带原因）
- [ ] 退款管理
  - [ ] 退款申请列表 — 待处理/已完成/已驳回
  - [ ] 退款审核 — 通过/驳回（驳回需填写理由）
  - [ ] 退款详情 — 退款原因、凭证、退款金额
- [ ] 订单设置
  - [ ] 超时未支付自动取消 — 可配置时间
  - [ ] 发货后自动收货时间 — 可配置
  - [ ] 收货后自动好评时间 — 可配置
  - [ ] 退款超时自动处理 — 可配置

### 📋 第二阶段：会员管理（待开发）
- [ ] 会员列表
  - [ ] 会员查询 — 用户名、手机号、等级、状态筛选
  - [ ] 会员详情 — 基本信息、账户余额、积分、等级、订单统计
  - [ ] 会员状态 — 启用/禁用账号
  - [ ] 修改资料 — 管理员修改会员信息
  - [ ] 余额调整 — 增加/减少余额、调整记录
  - [ ] 积分调整 — 增加/减少积分、调整记录
- [ ] 会员等级
  - [ ] 等级列表 — 等级名称、折扣、升级条件
  - [ ] 等级管理 — 新增/编辑/删除等级
  - [ ] 升级规则 — 消费金额/积分阈值配置
- [ ] 会员账户
  - [ ] 余额流水 — 充值、消费、退款记录
  - [ ] 积分流水 — 获取、消费、过期记录

### 📋 第三阶段：通用能力（待开发）
- [ ] 文件管理
  - [ ] 文件上传 — 图片/文档/视频
  - [ ] 文件列表 — 查询、预览、删除
  - [ ] 存储策略 — 本地 / 阿里云 OSS / MinIO 可配置
- [ ] 系统配置
  - [ ] 配置项管理 — 在线增删改查
  - [ ] 配置分类 — 按模块分组
  - [ ] 缓存刷新 — 修改后实时生效
- [ ] 操作日志
  - [ ] 操作记录 — 查询、详情
  - [ ] 按模块/操作人/时间筛选

### 📋 第三阶段：对外接口 & 工具（待开发）
- [ ] cms-api 模块
  - [ ] 对外 RESTful API — 商品、订单、会员开放接口
  - [ ] API 鉴权 — AppKey + AppSecret 签名方式
- [ ] MCP 服务
  - [ ] MCP server — 供智能体调用的工具集
  - [ ] 商品查询、订单查询、会员查询等 MCP tools
- [ ] 代码生成器
  - [ ] 根据数据库表生成 CRUD 代码
  - [ ] 生成 entity、mapper、service、controller、dto、vo
  - [ ] 生成前端页面（列表 + 表单弹窗）

## 权限模型

用户 → 角色 → 权限（RBAC 三级），多角色取并集。

```
用户 (sys_user)
  └── 多对多 ─── 角色 (sys_role)
                    └── 多对多 ─── 权限 (sys_permission)
```

**权限类型：**
- **目录**（type=1）：左侧菜单分类
- **菜单**（type=2）：可进入的页面，对应前端路由
- **资源**（type=3）：接口资源路径，Ant 风格通配，如 `/api/system/user/**`

**前端菜单：** 根据用户角色关联的权限（目录+菜单）动态渲染侧边栏和路由，无权限的菜单不显示、路由不注册。

**后端鉴权：** 采用角色驱动的注解方式，`@PreAuthorize("hasRole('xxx')")`，系统管理模块默认 `hasRole('admin')`，其他模块默认登录即可访问。

**认证方式：** Bearer Token + Redis，无状态，支持分布式集群，Token 有效期 2 小时（滑动续期）。

**超级管理员：**
- admin 用户 + admin 角色
- admin 角色不可删除、不可修改编码、不可禁用

## 项目文档
- [设计文档](docs/superpowers/specs/2026-08-24-cms-scaffolding-design.md)
- [第一阶段实现计划](docs/superpowers/plans/2026-08-24-phase1-rbac-scaffolding.md)

## 开发规范

### 后端
- 统一返回格式：`Result<T>` { code, message, data }
- 统一分页格式：`PageResult<T>` { list, total, pageNum, pageSize }
- 代码分层：controller → service → mapper
- 实体分层：entity（数据库实体）、dto（入参传输）、vo（出参视图）
- 逻辑删除：deleted 字段，MyBatis-Plus 自动处理
- 基础实体：`BaseEntity` 包含 id、createTime、updateTime、createBy、updateBy、deleted
- 后端鉴权：`@PreAuthorize("hasRole('角色编码')")`，角色驱动
- 认证方式：Bearer Token + Redis，无状态分布式认证

### 前端
- 页面组件放在 `views/`，按业务模块分目录
- API 请求封装在 `api/`，按模块分文件
- 工具函数在 `utils/`
- 状态管理用 Pinia，存在 `stores/`
- 路径别名：`@` 指向 `src/`
- 组件按需自动引入（unplugin-vue-components + ArcoResolver）
- 自动导入 Vue / Vue Router / Pinia API

## License
MIT
