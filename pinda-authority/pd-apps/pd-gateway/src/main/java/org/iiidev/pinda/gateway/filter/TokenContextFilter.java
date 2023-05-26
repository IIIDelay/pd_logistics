package org.iiidev.pinda.gateway.filter;

import org.iiidev.pinda.auth.client.properties.AuthClientProperties;
import org.iiidev.pinda.auth.client.utils.JwtTokenClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 当前过滤器负责解析请求头中的jwt令牌并且将解析出的用户信息放入zuul的header中
 */
@Component
public class TokenContextFilter extends BaseFilter {

    @Autowired
    private AuthClientProperties authClientProperties;

    @Autowired
    private JwtTokenClientUtils jwtTokenClientUtils;
}
