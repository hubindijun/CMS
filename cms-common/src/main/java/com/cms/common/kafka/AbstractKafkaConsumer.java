package com.cms.common.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;

/**
 * Kafka 消费者抽象基类
 * <p>子类继承后实现 getTopic()、getPayloadType()、handle() 三个方法即可，
 * 由本模板统一处理 JSON 反序列化、异常捕获与日志记录。</p>
 *
 * <pre>
 * &#64;Component
 * public class OrderConsumer extends AbstractKafkaConsumer&lt;OrderMessage&gt; {
 *     &#64;Override
 *     protected String getTopic() { return "order-topic"; }
 *
 *     &#64;Override
 *     protected Class&lt;OrderMessage&gt; getPayloadType() { return OrderMessage.class; }
 *
 *     &#64;Override
 *     protected void handle(OrderMessage payload, Acknowledgment ack) {
 *         // 业务消费逻辑
 *         ack.acknowledge();
 *     }
 *
 *     &#64;KafkaListener(topics = "#{__listener.getTopic()}")
 *     public void listen(String message, Acknowledgment ack) {
 *         consume(message, ack);
 *     }
 * }
 * </pre>
 */
@Slf4j
public abstract class AbstractKafkaConsumer<T> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 消费主题
     */
    protected abstract String getTopic();

    /**
     * 消息体类型
     */
    protected abstract Class<T> getPayloadType();

    /**
     * 业务消费逻辑
     *
     * @param payload  反序列化后的消息体
     * @param ack      手动确认对象（ack-mode: manual 时使用）
     */
    protected abstract void handle(T payload, Acknowledgment ack);

    /**
     * 模板方法：统一处理反序列化、异常捕获、日志记录
     *
     * @param message 原始消息字符串
     * @param ack     手动确认对象
     */
    protected void consume(String message, Acknowledgment ack) {
        long start = System.currentTimeMillis();
        try {
            T payload = objectMapper.readValue(message, getPayloadType());
            log.info("[Kafka消费] topic={}, payload={}", getTopic(), payload);
            handle(payload, ack);
            log.info("[Kafka消费] topic={}, 耗时={}ms", getTopic(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("[Kafka消费异常] topic={}, message={}, 耗时={}ms",
                    getTopic(), message, System.currentTimeMillis() - start, e);
            handleException(message, e);
        }
    }

    /**
     * 异常处理钩子方法，子类可重写实现告警、死信队列等逻辑
     *
     * @param message   原始消息
     * @param exception 异常
     */
    protected void handleException(String message, Exception exception) {
        // 默认只记录日志，子类可重写
    }
}
