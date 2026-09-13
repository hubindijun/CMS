package com.cms.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存操作服务
 * <p>使用方式：在业务 Service 中注入 {@link RedisService} 即可。</p>
 */
@Component
@ConditionalOnClass(StringRedisTemplate.class)
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== String 操作 ====================

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 值不存在时才设置
     *
     * @return true=设置成功 false=已存在
     */
    public Boolean setIfAbsent(String key, String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public String getAndSet(String key, String value) {
        return stringRedisTemplate.opsForValue().getAndSet(key, value);
    }

    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取剩余过期时间（秒）
     *
     * @return -1=没有过期时间，-2=key不存在
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public Long incr(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    public Long decr(String key, long delta) {
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    // ==================== 对象操作（JSON 序列化） ====================

    public void setObject(String key, Object obj) {
        try {
            set(key, objectMapper.writeValueAsString(obj));
        } catch (Exception e) {
            throw new RuntimeException("Redis 序列化对象失败", e);
        }
    }

    public void setObject(String key, Object obj, long timeout, TimeUnit unit) {
        try {
            set(key, objectMapper.writeValueAsString(obj), timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException("Redis 序列化对象失败", e);
        }
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String json = get(key);
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Redis 反序列化对象失败", e);
        }
    }

    public <T> T getObject(String key, TypeReference<T> typeReference) {
        String json = get(key);
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Redis 反序列化对象失败", e);
        }
    }

    // ==================== Hash 操作 ====================

    public void hSet(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    public void hSetAll(String key, Map<String, String> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    public String hGet(String key, String field) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    public Map<Object, Object> hGetAll(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public Long hDel(String key, Object... fields) {
        return stringRedisTemplate.opsForHash().delete(key, fields);
    }

    public Set<Object> hKeys(String key) {
        return stringRedisTemplate.opsForHash().keys(key);
    }

    public Long hLen(String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    public Long hIncrBy(String key, String field, long delta) {
        return stringRedisTemplate.opsForHash().increment(key, field, delta);
    }

    // ==================== List 操作 ====================

    public Long lPush(String key, String... values) {
        return stringRedisTemplate.opsForList().leftPushAll(key, values);
    }

    public Long rPush(String key, String... values) {
        return stringRedisTemplate.opsForList().rightPushAll(key, values);
    }

    public String lPop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public String rPop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public List<String> lRange(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    public Long lSize(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    // ==================== Set 操作 ====================

    public Long sAdd(String key, String... members) {
        return stringRedisTemplate.opsForSet().add(key, members);
    }

    public Long sRemove(String key, Object... members) {
        return stringRedisTemplate.opsForSet().remove(key, members);
    }

    public Set<String> sMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public Boolean sIsMember(String key, Object member) {
        return stringRedisTemplate.opsForSet().isMember(key, member);
    }

    public Long sSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }
}
