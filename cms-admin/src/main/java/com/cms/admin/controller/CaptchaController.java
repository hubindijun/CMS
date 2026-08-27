package com.cms.admin.controller;

import com.cms.admin.util.CaptchaService;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码接口
 * <p>
 * 提供登录图形验证码的生成接口。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 生成图形验证码
     *
     * @return 验证码信息（base64 图片 + key）
     */
    @GetMapping("/captcha")
    public Result<CaptchaService.CaptchaVO> captcha() {
        return Result.ok(captchaService.generate());
    }
}
