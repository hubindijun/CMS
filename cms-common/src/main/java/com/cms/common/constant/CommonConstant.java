package com.cms.common.constant;

/**
 * 通用常量
 */
public interface CommonConstant {
    /** 超级管理员角色编码 */
    String ROOT_ROLE_CODE = "root";
    /** 用户默认密码 */
    String DEFAULT_PASSWORD = "123456";
    /** 验证码 Redis key 前缀 */
    String CAPTCHA_PREFIX = "captcha:";
    /** 验证码过期时间（秒） */
    long CAPTCHA_EXPIRE = 300;
    /** 登录最大重试次数 */
    int LOGIN_MAX_RETRY = 5;
    /** 登录锁定 Redis key 前缀 */
    String LOGIN_LOCK_PREFIX = "login:lock:";
    /** 登录锁定时长（秒） */
    long LOGIN_LOCK_TIME = 600;
}
