package com.cms.admin.security;

import com.cms.admin.util.CaptchaUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class CaptchaFilter extends OncePerRequestFilter {

    private final CaptchaUtil captchaUtil;
    private final LoginAttemptService loginAttemptService;

    public CaptchaFilter(CaptchaUtil captchaUtil, LoginAttemptService loginAttemptService) {
        this.captchaUtil = captchaUtil;
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
            if (!captchaUtil.verify(captchaKey, captcha)) {
                loginAttemptService.incrementFail(username);
                throw new BadCredentialsException("验证码错误");
            }
        }
        filterChain.doFilter(request, response);
    }
}
