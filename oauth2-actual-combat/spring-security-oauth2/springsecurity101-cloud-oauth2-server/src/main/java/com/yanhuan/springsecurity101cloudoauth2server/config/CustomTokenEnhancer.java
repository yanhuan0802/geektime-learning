package com.yanhuan.springsecurity101cloudoauth2server.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义令牌增强器
 * <p>
 * 把用户信息嵌入到 JWT Token 中去（如果使用的是客户端凭据许可类型，这段代码无效，因为和用户没关系）。
 * 因为，默认情况下 Token 中只会有用户名这样的基本信息，
 * 我们往往需要把关于用户的更多信息返回给客户端（在实际应用中，你可能会从数据库或外部服务查询更多的用户信息加入到 JWT Token 中去）。
 * 这个时候，我们就可以自定义增强器来丰富 Token 的内容：
 *
 * @author YanHuan
 * @date 2020-11-07 16:18
 */
public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Authentication userAuthentication = authentication.getUserAuthentication();
        if (userAuthentication != null) {
            Object principal = authentication.getUserAuthentication().getPrincipal();
            //把用户标识嵌入JWT Token中去(Key是userDetails)
            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("userDetails", principal);
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        }
        return accessToken;
    }
}
