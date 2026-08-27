package com.cms.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
/**
 * 登录请求参数
 */
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String captcha;
    @NotBlank(message = "验证码key不能为空")
    private String captchaKey;
    private Boolean rememberMe;
}
