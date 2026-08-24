package com.cms.common.constant;

public interface CommonConstant {
    String ROOT_ROLE_CODE = "root";
    String DEFAULT_PASSWORD = "123456";
    String CAPTCHA_PREFIX = "captcha:";
    long CAPTCHA_EXPIRE = 300;
    int LOGIN_MAX_RETRY = 5;
    String LOGIN_LOCK_PREFIX = "login:lock:";
    long LOGIN_LOCK_TIME = 600;
}
