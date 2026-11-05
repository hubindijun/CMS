package com.cms.common.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Kafka 消息发送服务
 * <p>统一封装 KafkaTemplate，提供同步/异步发送，String/对象自动 JSON 序列化，统一日志埋点。</p>
 *
 * <p>使用方式：在业务 Service 中注入 {@link KafkaProducerService} 即可。</p>
 */
@Slf4j
@Component
@ConditionalOnClass(KafkaTemplate.class)
@RequiredArgsConstructor
public class KafkaProducerService {

    /** 同步发送默认超时时间（毫秒） */
    private static final long DEFAULT_TIMEOUT = 10_000L;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== 同步发送 ====================

    /**
     * 同步发送消息
     * <p>String 类型直接发送，其他类型自动 JSON 序列化。默认超时 10 秒。</p>
     *
     * @param topic   主题
     * @param message 消息内容
     * @return 发送结果
     */
    public SendResult<String, String> send(String topic, Object message) {
        String payload = serialize(message);
        log.info("[Kafka发送] topic={}, payload={}", topic, payload);
        long start = System.currentTimeMillis();
        try {
            SendResult<String, String> result = kafkaTemplate.send(topic, payload)
                    .get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            log.debug("[Kafka发送成功] topic={}, partition={}, offset={}, 耗时={}ms",
                    topic, result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("[Kafka发送失败] topic={}, payload={}, 异常={}",
                    topic, payload, e.getMessage(), e);
            throw new RuntimeException("Kafka 发送消息失败", e);
        }
    }

    /**
     * 同步发送带 key 的消息
     * <p>String 类型直接发送，其他类型自动 JSON 序列化。相同 key 的消息会进入同一个分区，保证顺序。</p>
     *
     * @param topic   主题
     * @param key     消息 key（用于分区路由，相同 key 保证同分区有序）
     * @param message 消息内容
     * @return 发送结果
     */
    public SendResult<String, String> send(String topic, String key, Object message) {
        String payload = serialize(message);
        log.info("[Kafka发送] topic={}, key={}, payload={}", topic, key, payload);
        long start = System.currentTimeMillis();
        try {
            SendResult<String, String> result = kafkaTemplate.send(topic, key, payload)
                    .get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            log.debug("[Kafka发送成功] topic={}, key={}, partition={}, offset={}, 耗时={}ms",
                    topic, key, result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("[Kafka发送失败] topic={}, key={}, payload={}, 异常={}",
                    topic, key, payload, e.getMessage(), e);
            throw new RuntimeException("Kafka 发送消息失败", e);
        }
    }

    // ==================== 异步发送（返回 CompletableFuture） ====================

    /**
     * 异步发送消息
     * <p>String 类型直接发送，其他类型自动 JSON 序列化。</p>
     *
     * @param topic   主题
     * @param message 消息内容
     * @return CompletableFuture
     */
    public CompletableFuture<SendResult<String, String>> sendAsync(String topic, Object message) {
        String payload = serialize(message);
        log.info("[Kafka发送] topic={}, payload={}", topic, payload);
        long start = System.currentTimeMillis();
        return kafkaTemplate.send(topic, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka发送失败] topic={}, payload={}, 异常={}",
                                topic, payload, ex.getMessage(), ex);
                    } else {
                        log.debug("[Kafka发送成功] topic={}, partition={}, offset={}, 耗时={}ms",
                                topic, result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                System.currentTimeMillis() - start);
                    }
                });
    }

    /**
     * 异步发送带 key 的消息
     * <p>String 类型直接发送，其他类型自动 JSON 序列化。相同 key 的消息会进入同一个分区，保证顺序。</p>
     *
     * @param topic   主题
     * @param key     消息 key（用于分区路由，相同 key 保证同分区有序）
     * @param message 消息内容
     * @return CompletableFuture
     */
    public CompletableFuture<SendResult<String, String>> sendAsync(String topic, String key, Object message) {
        String payload = serialize(message);
        log.info("[Kafka发送] topic={}, key={}, payload={}", topic, key, payload);
        long start = System.currentTimeMillis();
        return kafkaTemplate.send(topic, key, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka发送失败] topic={}, key={}, payload={}, 异常={}",
                                topic, key, payload, ex.getMessage(), ex);
                    } else {
                        log.debug("[Kafka发送成功] topic={}, key={}, partition={}, offset={}, 耗时={}ms",
                                topic, key, result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                System.currentTimeMillis() - start);
                    }
                });
    }

    // ==================== 异步发送（带回调） ====================

    /**
     * 异步发送消息（带成功/失败回调）
     *
     * @param topic     主题
     * @param message   消息内容
     * @param onSuccess 成功回调
     * @param onFailure 失败回调
     */
    public void sendAsync(String topic, Object message,
                          Consumer<SendResult<String, String>> onSuccess,
                          Consumer<Throwable> onFailure) {
        sendAsync(topic, message).whenComplete((result, ex) -> {
            if (ex != null) {
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            } else {
                if (onSuccess != null) {
                    onSuccess.accept(result);
                }
            }
        });
    }

    /**
     * 异步发送带 key 的消息（带成功/失败回调）
     *
     * @param topic     主题
     * @param key       消息 key
     * @param message   消息内容
     * @param onSuccess 成功回调
     * @param onFailure 失败回调
     */
    public void sendAsync(String topic, String key, Object message,
                          Consumer<SendResult<String, String>> onSuccess,
                          Consumer<Throwable> onFailure) {
        sendAsync(topic, key, message).whenComplete((result, ex) -> {
            if (ex != null) {
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            } else {
                if (onSuccess != null) {
                    onSuccess.accept(result);
                }
            }
        });
    }

    // ==================== 私有方法 ====================

    private String serialize(Object message) {
        if (message instanceof String) {
            return (String) message;
        }
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            throw new RuntimeException("Kafka 序列化消息失败", e);
        }
    }
}
