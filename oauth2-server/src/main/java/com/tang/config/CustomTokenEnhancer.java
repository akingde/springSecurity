package com.tang.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的Token增强器，可以把更多的信息放入token中（AccessToken）
 */
public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Authentication userAuthentication = authentication.getUserAuthentication();
        if (userAuthentication != null) {
            Object principal = authentication.getUserAuthentication().getPrincipal();
            //把用户标识嵌入JWT Token中去(Key是userDetails)
            Map<String, Object> additionalInfo = new HashMap<>(16);
            additionalInfo.put("userDetails", principal);
            additionalInfo.put("test", "这是自定义的信息");
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        }
        return accessToken;
    }
}