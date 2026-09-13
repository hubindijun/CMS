package com.cms.admin.controller;

import com.cms.admin.service.AuthService;
import com.cms.admin.vo.UserInfoVO;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证相关接口
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String captcha,
                                @RequestParam String captchaKey) {
        String token = authService.login(username, password, captcha, captchaKey);
        return Result.ok(token);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        authService.logout(authHeader);
        return Result.ok();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/user-info")
    public Result<UserInfoVO> userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserInfoVO vo = authService.getUserInfo(username);
        return Result.ok(vo);
    }
}
