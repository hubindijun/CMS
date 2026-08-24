package com.cms.admin.util;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import com.cms.common.constant.CommonConstant;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CaptchaUtil {

    private final StringRedisTemplate redisTemplate;

    public CaptchaVO generate() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String key = IdUtil.fastSimpleUUID();
        String code = captcha.getCode();
        redisTemplate.opsForValue().set(CommonConstant.CAPTCHA_PREFIX + key, code,
                CommonConstant.CAPTCHA_EXPIRE, TimeUnit.SECONDS);
        CaptchaVO vo = new CaptchaVO();
        vo.setKey(key);
        vo.setImage(captcha.getImageBase64Data());
        return vo;
    }

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
