package com.tang.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 * 配置 OAuth2RestTemplate Bean，并启用 OAuth2Sso 功能
 * 注解 @EnableOAuth2Sso 包含了 @EnableOAuth2Client
 */
@Configuration
@EnableOAuth2Sso
public class OAuthClientConfig {
    /**
     * 定义了OAuth2RestTemplate，网上一些比较老的资料给出的是手动读取配置文件来实现，
     * 最新版本已经可以自动注入OAuth2ProtectedResourceDetails
     *
     * @param oAuth2ClientContext 客户端上下文
     * @param details             资源详情
     * @return OAuth2RestTemplate
     */
    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oAuth2ClientContext,
                                                 OAuth2ProtectedResourceDetails details) {
        return new OAuth2RestTemplate(details, oAuth2ClientContext);
    }
}