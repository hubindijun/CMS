package com.cms.admin.controller;

import com.cms.admin.util.CaptchaUtil;
import com.cms.common.base.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaUtil captchaUtil;

    @GetMapping("/captcha")
    public Result<CaptchaUtil.CaptchaVO> captcha() {
        return Result.ok(captchaUtil.generate());
    }
}
