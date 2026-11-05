package com.cms.admin.job;

import com.cms.common.kafka.KafkaProducerService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kafka 健康检查定时任务
 * <p>每天 12:00 发送一条健康检查消息到 Kafka，验证生产者和消费者链路是否正常。</p>
 *
 * <p>XXL-Job 管理后台配置：
 * <ul>
 *   <li>JobHandler：kafkaHealthCheckHandler</li>
 *   <li>Cron：0 0 12 * * ?（每天 12:00）</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHealthCheckJob {

    private final KafkaProducerService kafkaProducerService;

    /**
     * Kafka 健康检查
     * <p>发送一条提示消息到 system.health.check topic。</p>
     */
    @XxlJob("kafkaHealthCheckHandler")
    public void kafkaHealthCheck() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message = "Kafka 生产者一切正常 - " + time;
        log.info("[健康检查] 开始发送 Kafka 健康检查消息：{}", message);
        kafkaProducerService.sendAsync("system.health.check", message);
        log.info("[健康检查] Kafka 健康检查消息已发送");
    }
}
