# Kafka 3 节点集群搭建指南（Docker Compose）

基于 KRaft 模式（无 ZooKeeper），3 个 Broker 节点，附带 Kafka UI 可视化管理界面。

## 集群架构

| 节点 | 容器名 | 对外端口 | Node ID | 角色 |
|------|--------|----------|---------|------|
| kafka-0 | kafka-0 | 19092 | 0 | controller + broker |
| kafka-1 | kafka-1 | 19093 | 1 | controller + broker |
| kafka-2 | kafka-2 | 19094 | 2 | controller + broker |
| Redpanda Console | kafka-ui | 8080 | - | 可视化管理 |

**Bootstrap Servers（本地连接）：** `localhost:19092,localhost:19093,localhost:19094`

**管理界面：** http://localhost:8091

## 前置要求

- Docker 20.10+
- Docker Compose v2+
- 至少 4GB 可用内存

## 快速开始

### 1. 启动集群

```bash
cd kafka-poc
docker-compose up -d
```

首次启动会拉取镜像并初始化，约需 1-2 分钟。

### 2. 查看启动状态

```bash
# 查看容器状态
docker-compose ps

# 查看启动日志
docker-compose logs -f kafka-0
```

当看到 `Kafka Server started` 日志说明启动成功。

### 3. 验证集群

**方式一：通过 Kafka UI**

浏览器打开 http://localhost:8091，在 Brokers 列表中应能看到 3 个 broker。

**方式二：命令行验证**

```bash
# 进入任意一个 broker 容器
docker exec -it kafka-0 /bin/bash

# 查看 broker 列表
kafka-metadata-quorum.sh --bootstrap-server localhost:9092 describe --status

# 创建测试 topic（副本因子 3，分区 3）
kafka-topics.sh --bootstrap-server localhost:9092 --create \
  --topic test-topic --partitions 3 --replication-factor 3

# 查看 topic 详情
kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic test-topic

# 生产消息
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test-topic
# 输入消息内容，Ctrl+C 退出

# 消费消息（另开一个终端）
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning
```

### 4. 停止集群

```bash
# 停止并保留数据
docker-compose down

# 停止并删除所有数据（彻底清理）
docker-compose down -v
```

## Spring Boot 项目配置

在 `application-dev.yml` 中修改 Kafka 配置：

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:19092,localhost:19093,localhost:19094
    producer:
      acks: all
      retries: 3
      batch-size: 16384
      linger-ms: 10
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      properties:
        enable.idempotence: true
    consumer:
      group-id: cms-admin-group
      auto-offset-reset: latest
      max-poll-records: 100
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    listener:
      ack-mode: manual_immediate
      concurrency: 3
      poll-timeout: 3000
```

## 常用操作

### 查看 topic 列表

```bash
docker exec kafka-0 kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### 查看消费组状态

```bash
docker exec kafka-0 kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
docker exec kafka-0 kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --describe --group cms-admin-group
```

### 模拟 broker 故障

```bash
# 停掉一个 broker，观察集群是否仍正常
docker stop kafka-2

# 恢复
docker start kafka-2
```

### 查看分区分布

```bash
docker exec kafka-0 kafka-topics.sh --bootstrap-server localhost:9092 \
  --describe --topic system.health.check
```

## 配置说明

### KRaft 模式

本集群使用 KRaft 模式，不再依赖 ZooKeeper。3 个节点同时承担 controller 和 broker 角色，适合开发测试环境。生产环境建议 controller 和 broker 分离部署。

### 副本策略

- `default.replication.factor = 3`：新建 topic 默认 3 副本
- `offsets.topic.replication.factor = 3`：消费者位移 topic 3 副本
- `transaction.state.log.replication.factor = 3`：事务日志 3 副本
- `min.insync.replicas = 2`：ISR 最少 2 个（配合 `acks=all` 保证数据不丢）

### 监听器说明

- `PLAINTEXT://kafka-0:9092`：集群内部通信（docker 网络内）
- `EXTERNAL://localhost:19092`：宿主机客户端连接
- `CONTROLLER://kafka-0:9093`：KRaft controller 间通信

## 常见问题

### 1. 启动后连不上 broker

检查 `advertised.listeners` 配置是否正确，客户端连接的地址必须与 advertised 地址一致。

### 2. 数据持久化

数据存在 Docker volume 中，`docker-compose down` 不会删除数据，加 `-v` 才会清空。

### 3. 内存不足

3 个 broker 加 UI 约占 2-3GB 内存，内存不够可以减少 broker 数量或调整 JVM 参数：

```yaml
environment:
  KAFKA_HEAP_OPTS: "-Xmx512m -Xms512m"
```

### 4. 不需要 Redpanda Console

直接注释掉 `docker-compose.yml` 中的 `kafka-ui` 服务即可。
