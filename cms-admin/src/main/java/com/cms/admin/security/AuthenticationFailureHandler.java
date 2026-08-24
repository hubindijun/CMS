package com.cms.admin.security;

import com.cms.common.base.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String msg = "登录失败";
        if (exception instanceof BadCredentialsException) {
            msg = "用户名或密码错误";
        } else if (exception instanceof DisabledException) {
            msg = "账号已禁用";
        } else if (exception instanceof LockedException) {
            msg = "账号已锁定，请稍后再试";
        } else if (exception.getMessage() != null) {
            msg = exception.getMessage();
        }
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(msg)));
    }
}
