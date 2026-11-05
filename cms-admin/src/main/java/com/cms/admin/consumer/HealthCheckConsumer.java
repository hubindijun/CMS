package com.cms.admin.consumer;

import com.cms.common.kafka.BaseKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Kafka 健康检查消费者
 * <p>订阅 system.health.check topic，接收健康检查消息并打印日志，
 * 用于验证 Kafka 生产者到消费者的整条链路是否正常。</p>
 */
@Slf4j
@Component
public class HealthCheckConsumer extends BaseKafkaConsumer {

    @Override
    protected String getTopic() {
        return "system.health.check";
    }

    @Override
    protected void handle(String message) {
        log.info("收到健康检查消息：{}", message);
        log.info("消费者接收消息正常，消费者处理正常");
    }
}
