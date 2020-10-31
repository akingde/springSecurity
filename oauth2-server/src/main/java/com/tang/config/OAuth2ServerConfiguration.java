package com.tang.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 配置授权服务器，OAuth2 服务相关配置
 * 注解 @EnableAuthorizationServer：【开启授权服务器】
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * OAuth2 客户端信息配置
     * 我们配置了使用数据库来维护客户端信息。虽然在各种Demo中我们经常看到的是在内存中维护客户端信息，通过配置直接写死在这里。
     * 但是，对于实际的应用我们一般都会用数据库来维护这个信息，甚至还会建立一套工作流来允许客户端自己申请ClientID，实现OAuth客户端接入的审批。
     *
     * @param clients 客户端
     * @throws Exception 异常信息
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    /**
     * 授权服务器安全配置，这里干了两件事儿。
     * 1、首先，打开了验证Token的访问权限（以便之后我们演示）。
     * 2、然后，允许ClientSecret明文方式保存，并且可以通过表单提交（而不仅仅是Basic Auth方式提交），之后会演示到这个。
     *
     * @param security 安全配置
     * @throws Exception 异常信息
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()")
                // 允许表单提交认证
                .allowFormAuthenticationForClients()
                // 密码解析
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    /**
     * 授权服务器终结点配置器，干了以下4件事儿：
     * 1. 配置我们的令牌存放方式为JWT方式，而不是内存、数据库或Redis方式。
     * JWT是Json Web Token的缩写，也就是使用JSON数据格式包装的令牌，由.号把整个JWT分隔为头、数据体、签名三部分。
     * JWT保存Token虽然易于使用但是不是那么安全，一般用于内部，且需要走HTTPS并配置比较短的失效时间。
     * 2. 配置JWT Token的非对称加密来进行签名
     * 3. 配置一个自定义的Token增强器，把更多信息放入Token中
     * 4. 配置使用JDBC数据库方式来保存用户的授权批准记录
     *
     * @param endpoints 终结点
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        // Token令牌增强器(自定义、非对称加密)
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(tokenEnhancer(), jwtTokenEnhancer()));

        // 存用户的授权批准记录
        endpoints.approvalStore(approvalStore())
                // 授权码的保存
                .authorizationCodeServices(authorizationCodeServices())
                // Token存储方式
                .tokenStore(tokenStore())
                // Token令牌增强器(对称/非对称加密处理)
                .tokenEnhancer(tokenEnhancerChain)
                // 身份验证管理器
                .authenticationManager(authenticationManager);
    }


    /**
     * 使用JDBC数据库方式来保存授权码
     *
     * @return 授权码相关
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    /**
     * 使用JWT存储
     *
     * @return Token存储方式
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    /**
     * 使用JDBC数据库方式来保存用户的授权批准记录
     *
     * @return 数据库存储
     */
    @Bean
    public JdbcApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    /**
     * 自定义的Token增强器，把更多信息放入Token中
     *
     * @return Token增强器
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    /**
     * 配置JWT使用非对称加密方式来验证
     *
     * @return JWT Token
     */
    @Bean
    protected JwtAccessTokenConverter jwtTokenEnhancer() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource("jwt.jks"), "mySecretKey".toCharArray());
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwt"));
        return converter;
    }

    /**
     * 配置登录页面的视图信息（其实可以独立一个配置类更规范）
     */
    @Configuration
    static class MvcConfig implements WebMvcConfigurer {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("login").setViewName("login");
        }
    }

}