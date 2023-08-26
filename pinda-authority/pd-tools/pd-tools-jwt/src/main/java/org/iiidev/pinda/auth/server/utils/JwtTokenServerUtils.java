package org.iiidev.pinda.auth.server.utils;

import org.iiidev.pinda.auth.server.properties.AuthServerProperties;
import org.iiidev.pinda.auth.utils.JwtHelper;
import org.iiidev.pinda.auth.utils.JwtUserInfo;
import org.iiidev.pinda.auth.utils.Token;
import org.iiidev.pinda.exception.BizException;

import lombok.AllArgsConstructor;

/**
 * jwt token 工具
 *
 */
@AllArgsConstructor
public class JwtTokenServerUtils {
    /**
     * 认证服务端使用，如 authority-server
     * 生成和 解析token
     */
    private AuthServerProperties authServerProperties;

    /**
     * 生成token
     * @param jwtInfo
     * @param expire
     * @return
     * @throws BizException
     */
    public Token generateUserToken(JwtUserInfo jwtInfo, Integer expire) {
        AuthServerProperties.TokenInfo userTokenInfo = authServerProperties.getUser();
        if (expire == null || expire <= 0) {
            expire = userTokenInfo.getExpire();
        }
        return JwtHelper.generateUserToken(jwtInfo, userTokenInfo.getPriKey(), expire);
    }

    /**
     * 解析token
     * @param token
     * @return
     * @throws BizException
     */
    public JwtUserInfo getUserInfo(String token) throws BizException {
        AuthServerProperties.TokenInfo userTokenInfo = authServerProperties.getUser();
        return JwtHelper.getJwtFromToken(token, userTokenInfo.getPubKey());
    }
}
