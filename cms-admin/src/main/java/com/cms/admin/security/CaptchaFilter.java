package com.cms.admin.security;

import com.cms.admin.util.CaptchaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * 验证码校验过滤器，在用户名密码认证前校验验证码及登录失败锁定状态
 */
@Component
public class CaptchaFilter extends OncePerRequestFilter {

    private final CaptchaService captchaService;
    private final LoginAttemptService loginAttemptService;

    public CaptchaFilter(CaptchaService captchaService, LoginAttemptService loginAttemptService) {
        this.captchaService = captchaService;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String username = request.getParameter("username");
            if (loginAttemptService.isLocked(username)) {
                throw new LockedException("账号已锁定，请10分钟后再试");
            }

            String captcha = request.getParameter("captcha");
            String captchaKey = request.getParameter("captchaKey");
            if (!captchaService.verify(captchaKey, captcha)) {
                loginAttemptService.incrementFail(username);
                throw new BadCredentialsException("验证码错误");
            }
        }
        filterChain.doFilter(request, response);
    }
}
