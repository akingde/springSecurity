package com.tang.oauth.token.granter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户名授权方式
 */
public class UsernameTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "username";

    private final UserDetailsService userDetailsService;


    /**
     * 构造方法提供一些必要的注入的参数
     * 通过这些参数来完成我们父类的构建
     */
    public UsernameTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                                OAuth2RequestFactory oAuth2RequestFactory, UserDetailsService userDetailsService) {
        super(tokenServices, clientDetailsService, oAuth2RequestFactory, GRANT_TYPE);
        this.userDetailsService = userDetailsService;
    }

    /**
     * 在这里查询我们用户，构建用户的授权信息
     */
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String username = parameters.get("username");

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new RuntimeException("用户不存在");
        }

        // 这里可以对用户名、密码等做校验，但是一般不会使用，
        // 因为如果使用用户名、密码做校验的话，直接使用自带的password验证即可
        Authentication userAuth = new UsernamePasswordAuthenticationToken(userDetails, "");
        OAuth2Request storedOauth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOauth2Request, userAuth);
    }


}
