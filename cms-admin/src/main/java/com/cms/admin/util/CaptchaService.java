package com.cms.admin.util;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import com.cms.common.constant.CommonConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务：生成图形验证码并存入 Redis，支持校验
 */
@Component
@RequiredArgsConstructor
public class CaptchaService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 生成验证码
     *
     * @return 验证码 key 和 base64 图片
     */
    public CaptchaVO generate() {
        LineCaptcha captcha = cn.hutool.captcha.CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String key = IdUtil.fastSimpleUUID();
        String code = captcha.getCode();
        redisTemplate.opsForValue().set(CommonConstant.CAPTCHA_PREFIX + key, code,
                CommonConstant.CAPTCHA_EXPIRE, TimeUnit.SECONDS);
        CaptchaVO vo = new CaptchaVO();
        vo.setKey(key);
        vo.setImage(captcha.getImageBase64Data());
        return vo;
    }

    /**
     * 校验验证码（校验通过后立即删除，一次性使用）
     *
     * @param key 验证码 key
     * @param code 用户输入的验证码
     * @return 是否校验通过
     */
    public boolean verify(String key, String code) {
        String cacheKey = CommonConstant.CAPTCHA_PREFIX + key;
        String cachedCode = redisTemplate.opsForValue().get(cacheKey);
        if (cachedCode == null) {
            return false;
        }
        redisTemplate.delete(cacheKey);
        return cachedCode.equalsIgnoreCase(code);
    }

    @Data
    public static class CaptchaVO {
        private String key;
        private String image;
    }
}
