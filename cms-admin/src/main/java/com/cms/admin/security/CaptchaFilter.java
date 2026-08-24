package com.cms.admin.security;

import com.cms.admin.util.CaptchaUtil;
import com.cms.common.constant.CommonConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaFilter extends OncePerRequestFilter {

    private final CaptchaUtil captchaUtil;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if ("/api/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String username = request.getParameter("username");
            String lockKey = CommonConstant.LOGIN_LOCK_PREFIX + username;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
                throw new BadCredentialsException("账号已锁定，请10分钟后再试");
            }

            String captcha = request.getParameter("captcha");
            String captchaKey = request.getParameter("captchaKey");
            if (!captchaUtil.verify(captchaKey, captcha)) {
                incrementLoginFail(username);
                throw new BadCredentialsException("验证码错误");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void incrementLoginFail(String username) {
        String key = CommonConstant.LOGIN_LOCK_PREFIX + username + ":fail";
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count >= CommonConstant.LOGIN_MAX_RETRY) {
            redisTemplate.opsForValue().set(CommonConstant.LOGIN_LOCK_PREFIX + username, "locked",
                    CommonConstant.LOGIN_LOCK_TIME, TimeUnit.SECONDS);
            redisTemplate.delete(key);
        } else {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
    }
}
