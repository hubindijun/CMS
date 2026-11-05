# XXL-Job 调度中心搭建指南

## 架构说明

- **调度中心（xxl-job-admin）**：统一管理定时任务，提供 Web 管理界面
- **执行器（cms-admin）**：业务方应用，负责执行具体任务
- **数据库（MySQL 8.0）**：调度中心专用 MySQL，容器内独立部署，不与业务混用

## 组件清单

| 组件 | 容器名 | 端口 | 说明 |
|------|--------|------|------|
| MySQL 8.0 | xxl-job-mysql | 3307（宿主机） | 调度中心专用数据库，root/123456 |
| XXL-Job Admin | xxl-job-admin | 8092（宿主机） | 调度中心管理界面 |

**管理后台：** http://localhost:8092/xxl-job-admin
**默认账号：** admin / 123456

## 启动调度中心

```bash
cd xxl-job-poc
docker-compose up -d
```

启动顺序：先起 MySQL → 自动创建 `xxl_job` 库 → XXL-Job Admin 启动并自动建表。
首次启动约需 1-2 分钟，等待初始化完成。

**查看启动状态：**
```bash
docker-compose logs -f xxl-job-admin
```
看到 `xxl-job admin start success.` 即启动成功。

## 配置执行器

cms-admin 项目已内置执行器，配置在 `application-dev.yml` 中：

```yaml
xxl:
  job:
    admin:
      addresses: http://localhost:8092/xxl-job-admin  # 调度中心地址
    executor:
      appname: cms-admin-executor   # 执行器名称
      port: 9999                      # 执行器端口
```

### 在调度中心添加执行器

1. 登录管理后台 → 执行器管理 → 新增执行器
2. 填写信息：
   - **AppName**：`cms-admin-executor`（与配置文件中一致）
   - **名称**：`CMS后台执行器`
   - **注册方式**：自动注册（推荐）
   - **机器地址**：留空（自动注册会自动发现）
3. 保存后，在"执行器管理"页面查看"在线机器地址"，能看到 cms-admin 的地址即注册成功

> 注意：cms-admin 必须先启动，执行器才会注册上来。

## 使用示例：Kafka 健康检查任务

cms-admin 中已内置 `kafkaHealthCheckHandler` 任务处理器。

### 新建任务

1. 任务管理 → 选择执行器 `cms-admin-executor` → 新增
2. 填写配置：
   - **任务描述**：Kafka 健康检查
   - **路由策略**：第一个
   - **Cron**：`0 0 12 * * ?`（每天 12:00 执行）
   - **运行模式**：BEAN
   - **JobHandler**：`kafkaHealthCheckHandler`
   - **任务参数**：留空
   - **调度状态**：运行
3. 保存

### 手动触发测试

在任务列表点击"执行一次" → 确认 → 查看执行日志：

- 调度中心：任务管理 → 操作列"执行日志"
- cms-admin 日志：应看到 `[Kafka发送]` 日志
- Kafka 消费者：应看到 `[Kafka消费]` 和 "收到健康检查消息" 日志

## 常用 Cron 表达式

| 表达式 | 含义 |
|--------|------|
| `0 * * * * ?` | 每分钟执行一次 |
| `0 0/5 * * * ?` | 每 5 分钟执行一次 |
| `0 0 * * * ?` | 每小时整点执行 |
| `0 0 12 * * ?` | 每天 12:00 执行 |
| `0 0 0 * * ?` | 每天凌晨执行 |
| `0 0 2 * * ?` | 每天凌晨 2 点执行 |

## 验证步骤

1. 启动调度中心：`docker-compose up -d`
2. 等待启动完成，访问 http://localhost:8092/xxl-job-admin 登录
3. 启动 cms-admin 后端
4. 执行器管理 → 查看 `cms-admin-executor` 的在线机器数
5. 任务管理 → 手动触发一次 Kafka 健康检查任务
6. 观察 cms-admin 日志和 Kafka 消费者日志

## 数据持久化

MySQL 数据存在 Docker volume `xxl-job-poc_mysql-data` 中，`docker-compose down` 不会删除数据。
彻底清理：
```bash
docker-compose down -v
```

## 常见问题

### 1. XXL-Job Admin 启动失败

查看日志：
```bash
docker-compose logs xxl-job-admin
```
常见原因：
- MySQL 还没启动完就开始连 — 等一会儿自动重试即可
- 数据库连接地址/账号密码不对

### 2. 执行器注册不上

检查：
- cms-admin 是否正常启动
- `xxl.job.admin.addresses` 地址是否正确（应为 `http://localhost:8092/xxl-job-admin`）
- 执行器和调度中心网络是否互通
- 查看 cms-admin 日志中 XXL-Job 相关报错

### 3. 任务执行失败

在调度中心"执行日志"中查看详细错误信息，常见原因：
- JobHandler 名称写错
- 执行器已下线
- 业务代码抛异常

### 4. 手动连接数据库调试

```bash
# 使用宿主机端口 3307 连接
mysql -h 127.0.0.1 -P 3307 -u root -p123456 xxl_job
```
