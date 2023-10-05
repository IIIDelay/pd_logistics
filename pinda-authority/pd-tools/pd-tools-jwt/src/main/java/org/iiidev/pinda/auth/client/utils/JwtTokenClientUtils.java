package org.iiidev.pinda.auth.client.utils;

import lombok.NoArgsConstructor;
import org.iiidev.pinda.auth.client.properties.AuthClientProperties;
import org.iiidev.pinda.auth.utils.JwtHelper;
import org.iiidev.pinda.auth.utils.JwtUserInfo;
import org.iiidev.pinda.exception.BizException;

import lombok.AllArgsConstructor;

/**
 * JwtToken 客户端工具
 *
 */
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenClientUtils {
    /**
     * 用于 认证服务的 客户端使用(如 网关) ， 在网关获取到token后，
     * 调用此工具类进行token 解析。
     * 客户端一般只需要解析token 即可
     */
    private AuthClientProperties authClientProperties;

    /**
     * 解析token
     *
     * @param token
     * @return
     * @throws BizException
     */
    public JwtUserInfo getUserInfo(String token) throws BizException {
        AuthClientProperties.TokenInfo userTokenInfo = authClientProperties.getUser();
        return JwtHelper.getJwtFromToken(token, userTokenInfo.getPubKey());
    }
}
