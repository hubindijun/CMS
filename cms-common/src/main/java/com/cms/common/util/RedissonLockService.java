package com.cms.common.util;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redisson 分布式锁服务
 * <p>使用方式：在业务 Service 中注入 {@link RedissonLockService} 即可。</p>
 */
@Component
@ConditionalOnClass(RedissonClient.class)
@RequiredArgsConstructor
public class RedissonLockService {

    private static final String LOCK_PREFIX = "lock:";

    private final RedissonClient redissonClient;

    /**
     * 尝试获取锁，不等待，立即返回
     *
     * @param lockKey 锁的 key
     * @return true=获取成功 false=获取失败
     */
    public boolean tryLock(String lockKey) {
        RLock lock = redissonClient.getLock(buildKey(lockKey));
        return lock.tryLock();
    }

    /**
     * 尝试获取锁，等待指定时间
     *
     * @param lockKey  锁的 key
     * @param waitTime 最大等待时间
     * @param unit     时间单位
     * @return true=获取成功 false=获取失败
     */
    public boolean tryLock(String lockKey, long waitTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(buildKey(lockKey));
        try {
            return lock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 尝试获取锁，等待指定时间，持锁超时自动释放
     *
     * @param lockKey   锁的 key
     * @param waitTime  最大等待时间
     * @param leaseTime 持锁超时时间（到期自动释放）
     * @param unit      时间单位
     * @return true=获取成功 false=获取失败
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(buildKey(lockKey));
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 阻塞获取锁（看门狗自动续期，默认 30 秒）
     *
     * @param lockKey 锁的 key
     */
    public void lock(String lockKey) {
        RLock lock = redissonClient.getLock(buildKey(lockKey));
        lock.lock();
    }

    /**
     * 阻塞获取锁，持锁超时自动释放
     *
     * @param lockKey   锁的 key
     * @param leaseTime 持锁超时时间
     * @param unit      时间单位
     */
    public void lock(String lockKey, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(buildKey(lockKey));
        lock.lock(leaseTime, unit);
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的 key
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(buildKey(lockKey));
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 判断锁是否被持有
     *
     * @param lockKey 锁的 key
     * @return true=被锁定
     */
    public boolean isLocked(String lockKey) {
        RLock lock = redissonClient.getLock(buildKey(lockKey));
        return lock.isLocked();
    }

    /**
     * 尝试获取锁并返回自动释放的锁句柄（支持 try-with-resources）
     *
     * <pre>
     * try (var ignore = lockService.tryLockWithAutoRelease("order:123", 3, 30, TimeUnit.SECONDS)) {
     *     // 业务逻辑
     * }
     * </pre>
     *
     * @param lockKey   锁的 key
     * @param waitTime  最大等待时间
     * @param leaseTime 持锁超时时间
     * @param unit      时间单位
     * @return 可自动关闭的锁句柄
     * @throws RuntimeException 获取锁失败时抛出
     */
    public LockHolder tryLockWithAutoRelease(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        if (tryLock(lockKey, waitTime, leaseTime, unit)) {
            return new LockHolder(lockKey, this);
        }
        throw new RuntimeException("获取分布式锁失败: " + lockKey);
    }

    private String buildKey(String lockKey) {
        return LOCK_PREFIX + lockKey;
    }

    /**
     * 锁持有器，支持 try-with-resources 自动释放
     */
    public static class LockHolder implements AutoCloseable {
        private final String lockKey;
        private final RedissonLockService lockService;
        private boolean released;

        private LockHolder(String lockKey, RedissonLockService lockService) {
            this.lockKey = lockKey;
            this.lockService = lockService;
        }

        @Override
        public void close() {
            if (!released) {
                lockService.unlock(lockKey);
                released = true;
            }
        }
    }
}
