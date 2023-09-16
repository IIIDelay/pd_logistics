package org.iiidev.pinda.common.constant;

import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.nio.charset.StandardCharsets;

/**
 * HttpConstant
 *
 * @Author IIIDelay
 * @Date 2023/9/16 8:18
 **/
@AllArgsConstructor
public enum HttpConstant {

    CONTENT_TYPE_APP_UTF8(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8).toString());

    public final String key;

    public final String value;
}
