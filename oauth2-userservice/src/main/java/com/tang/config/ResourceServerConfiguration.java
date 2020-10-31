package com.tang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

/**
 * 资源服务器配置类
 * 注解 EnableResourceServer 是为了【启用资源服务器】
 * 注解 EnableGlobalMethodSecurity 是为了【启用方法注解方式来进行权限控制】
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * 声明了资源服务器的ID是userservice，声明了资源服务器的TokenStore是JWT
     *
     * @param resources 资源服务安全
     * @throws Exception 异常信息
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("userservice").tokenStore(tokenStore());
    }

    /**
     * 配置了除了/user路径之外的请求可以匿名访问
     *
     * @param http HTTP安全
     * @throws Exception 异常信息
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .anyRequest().permitAll();
    }

    /**
     * 配置TokenStore,token的存储方式
     * 这里使用的是JWT，如果是redis或者mysql，更换具体的实现即可
     *
     * @return TokenStore
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 配置公钥
     * 用来验证【授权服务器】的token
     *
     * @return JwtAccessTokenConverter
     */
    @Bean
    protected JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        Resource resource = new ClassPathResource("public.cert");
        String publicKey = null;
        try {
            publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        converter.setVerifierKey(publicKey);
        return converter;
    }


}    