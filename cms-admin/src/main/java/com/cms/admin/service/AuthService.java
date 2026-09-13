package com.cms.admin.service;

import com.cms.admin.vo.UserInfoVO;

/**
 * 认证服务
 */
public interface AuthService {

    /**
     * 登录
     *
     * @param username  用户名
     * @param password  密码
     * @param captcha   验证码
     * @param captchaKey 验证码key
     * @return token
     */
    String login(String username, String password, String captcha, String captchaKey);

    /**
     * 登出
     *
     * @param authHeader Authorization header
     */
    void logout(String authHeader);

    /**
     * 获取当前登录用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserInfoVO getUserInfo(String username);
}
