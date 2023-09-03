package org.iiidev.pinda.auth.client.properties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import static org.iiidev.pinda.auth.client.properties.AuthClientProperties.PREFIX;
/**
 * 客户端认证配置
 */
@ConfigurationProperties(prefix = PREFIX)
@Data
@NoArgsConstructor
@RefreshScope
public class AuthClientProperties {
    public static final String PREFIX = "authentication";
    private TokenInfo user;
    @Data
    public static class TokenInfo {
        /**
         * 请求头名称
         */
        private String headerName;
        /**
         * 解密 网关使用
         */
        private String pubKey;
    }
}
