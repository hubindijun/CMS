package com.cms.common.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * Kafka 消费者抽象基类
 * <p>模板方法模式，统一处理日志记录、异常捕获与手动 ACK。
 * 消息体以原始 String 形式传给业务方，由业务方自行解析（JSON/Map/对象等）。</p>
 *
 * <pre>
 * &#64;Component
 * public class OrderConsumer extends BaseKafkaConsumer {
 *
 *     &#64;Override
 *     public String getTopic() {
 *         return "order.created";
 *     }
 *
 *     &#64;Override
 *     protected void handle(String message) {
 *         // 业务方自行解析消息：
 *         // Order order = JSONUtil.toBean(message, Order.class);
 *         // 或 JSONObject obj = JSONUtil.parseObj(message);
 *     }
 * }
 * </pre>
 *
 * <p>消费流程：接收消息 → 执行业务 handle() → ACK 确认。
 * 无论成功或失败都会 ACK，避免坏消息阻塞分区。失败时记录错误日志并调用
 * {@link #handleException(String, Exception)} 钩子方法，子类可重写实现告警、死信等逻辑。</p>
 *
 * <p>topic 通过 {@link #getTopic()} 由子类提供，基类通过 SpEL 表达式
 * <code>#{__listener.getTopic()}</code> 动态绑定到 {@link KafkaListener}。</p>
 */
@Slf4j
@KafkaListener(topics = "#{__listener.getTopic()}")
public abstract class BaseKafkaConsumer {

    /**
     * 订阅的 topic（SpEL 表达式 #{__listener.getTopic()} 调用，必须为 public）
     *
     * @return topic 名称
     */
    public abstract String getTopic();

    /**
     * 业务消费逻辑
     *
     * @param message 原始消息字符串，业务方自行解析
     */
    protected abstract void handle(String message);

    /**
     * 监听入口方法，由 Spring Kafka 调用
     *
     * @param message 原始消息字符串
     * @param ack     手动确认对象
     */
    public void listen(String message, Acknowledgment ack) {
        consume(message, ack);
    }

    /**
     * 模板方法：统一处理日志、异常捕获与 ACK
     *
     * @param message 原始消息字符串
     * @param ack     手动确认对象
     */
    private void consume(String message, Acknowledgment ack) {
        long start = System.currentTimeMillis();
        String topic = getTopic();
        try {
            log.info("[Kafka消费] topic={}, payload={}", topic, message);
            handle(message);
            log.debug("[Kafka消费成功] topic={}, 耗时={}ms", topic, System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("[Kafka消费失败] topic={}, payload={}, 耗时={}ms",
                    topic, message, System.currentTimeMillis() - start, e);
            try {
                handleException(message, e);
            } catch (Exception ex) {
                log.error("[Kafka消费] handleException 执行异常", ex);
            }
        } finally {
            ack.acknowledge();
        }
    }

    /**
     * 异常处理钩子方法，子类可重写实现告警、死信队列等逻辑
     * <p>默认空实现，仅由基类记录错误日志。重写时注意：本方法抛出的异常会被吞掉，
     * 不会影响 ACK 确认。</p>
     *
     * @param message   原始消息
     * @param exception 异常
     */
    protected void handleException(String message, Exception exception) {
        // 默认空实现，子类按需重写
    }
}
