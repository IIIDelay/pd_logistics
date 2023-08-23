package org.iiidev.pinda.authority.biz.service.auth.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Assert;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.iiidev.pinda.authority.biz.service.auth.ValidateCodeService;
import org.iiidev.pinda.authority.util.RedisHelper;
import org.iiidev.pinda.common.constant.CacheKey;
import org.iiidev.pinda.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * 验证码服务
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class ValidateCodeServiceImpl implements ValidateCodeService {

    @Override
    public void create(String key, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(key)) {
            throw BizException.validFail("验证码key不能为空");
        }
        // setHeader(response, "arithmetic");
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);

        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 45, 4, 10);
        RedisHelper.save(StringUtils.lowerCase(lineCaptcha.getCode()), Duration.ofSeconds(60), CacheKey.CAPTCHA, key);
        lineCaptcha.write(response.getOutputStream());
    }

    @Override
    public boolean check(String key, String value) {
        if (StringUtils.isBlank(value)) {
            throw BizException.validFail("请输入验证码");
        }

        String code = RedisHelper.getValue(CacheKey.CAPTCHA, key);
        Assert.notEmpty(code, () -> BizException.validFail("验证码已过期"));
        Assert.isFalse(!StringUtils.equalsIgnoreCase(value, code), () -> BizException.validFail("验证码不正确"));

        // 验证通过，立即从缓存中删除验证码
        RedisHelper.remove(CacheKey.CAPTCHA, key);
        return true;
    }

    private Captcha createCaptcha(String type) {
        Captcha captcha = null;
        if (StringUtils.equalsIgnoreCase(type, "gif")) {
            captcha = new GifCaptcha(115, 42, 4);
        } else if (StringUtils.equalsIgnoreCase(type, "png")) {
            captcha = new SpecCaptcha(115, 42, 4);
        } else if (StringUtils.equalsIgnoreCase(type, "arithmetic")) {
            captcha = new ArithmeticCaptcha(115, 42);
        } else if (StringUtils.equalsIgnoreCase(type, "chinese")) {
            captcha = new ChineseCaptcha(115, 42);
        }
        captcha.setCharType(2);
        return captcha;
    }

    private void setHeader(HttpServletResponse response, String type) {
        if (StringUtils.equalsIgnoreCase(type, "gif")) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        } else {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
    }
}
