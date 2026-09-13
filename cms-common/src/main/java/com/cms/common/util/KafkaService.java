package com.cms.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Kafka 消息发送服务
 * <p>使用方式：在业务 Service 中注入 {@link KafkaService} 即可。</p>
 */
@Component
@ConditionalOnClass(KafkaTemplate.class)
@RequiredArgsConstructor
public class KafkaService {

    private static final long DEFAULT_TIMEOUT = 10_000;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== 同步发送 ====================

    /**
     * 同步发送字符串消息
     *
     * @param topic   主题
     * @param message 消息内容
     */
    public SendResult<String, String> send(String topic, String message) {
        try {
            return kafkaTemplate.send(topic, message).get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Kafka 发送消息失败", e);
        }
    }

    /**
     * 同步发送带 key 的字符串消息
     *
     * @param topic   主题
     * @param key     消息 key（用于分区路由）
     * @param message 消息内容
     */
    public SendResult<String, String> send(String topic, String key, String message) {
        try {
            return kafkaTemplate.send(topic, key, message).get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Kafka 发送消息失败", e);
        }
    }

    /**
     * 同步发送对象消息（JSON 序列化）
     *
     * @param topic 主题
     * @param obj   消息对象
     */
    public SendResult<String, String> sendObject(String topic, Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return send(topic, json);
        } catch (Exception e) {
            throw new RuntimeException("Kafka 发送对象消息失败", e);
        }
    }

    /**
     * 同步发送带 key 的对象消息（JSON 序列化）
     *
     * @param topic 主题
     * @param key   消息 key
     * @param obj   消息对象
     */
    public SendResult<String, String> sendObject(String topic, String key, Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return send(topic, key, json);
        } catch (Exception e) {
            throw new RuntimeException("Kafka 发送对象消息失败", e);
        }
    }

    // ==================== 异步发送 ====================

    /**
     * 异步发送字符串消息
     *
     * @param topic   主题
     * @param message 消息内容
     * @return CompletableFuture
     */
    public CompletableFuture<SendResult<String, String>> sendAsync(String topic, String message) {
        return kafkaTemplate.send(topic, message);
    }

    /**
     * 异步发送带 key 的字符串消息
     *
     * @param topic   主题
     * @param key     消息 key
     * @param message 消息内容
     * @return CompletableFuture
     */
    public CompletableFuture<SendResult<String, String>> sendAsync(String topic, String key, String message) {
        return kafkaTemplate.send(topic, key, message);
    }

    /**
     * 异步发送对象消息（JSON 序列化）
     *
     * @param topic 主题
     * @param obj   消息对象
     * @return CompletableFuture
     */
    public CompletableFuture<SendResult<String, String>> sendObjectAsync(String topic, Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return sendAsync(topic, json);
        } catch (Exception e) {
            CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Kafka 序列化对象失败", e));
            return future;
        }
    }

    /**
     * 异步发送带 key 的对象消息（JSON 序列化）
     *
     * @param topic 主题
     * @param key   消息 key
     * @param obj   消息对象
     * @return CompletableFuture
     */
    public CompletableFuture<SendResult<String, String>> sendObjectAsync(String topic, String key, Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return sendAsync(topic, key, json);
        } catch (Exception e) {
            CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Kafka 序列化对象失败", e));
            return future;
        }
    }
}
