package com.tang.oauth.provider;

import com.tang.oauth.SecurityUserDetail;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 自定义用户验证
 * AuthenticationProvider是用户自定义身份认证,认证流程顶级接口。唯一作用即使用来进行身份验证，同时springSecurity也为我们提供了很多方便的实现类。
 * 当我们没有指定相关AuthenticationProvider 对象时springSecurity默认使用的就是DaoAuthenticationProvider进行验证。也就是最常见的账户名密码的方式。
 */
public class CustomUserAuthenticationProvider extends DaoAuthenticationProvider {

    @SneakyThrows
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");
            throw new RuntimeException("no credentials provided");
        }

        String password = authentication.getCredentials().toString();
        //获取传入用户名
        String username = authentication.getPrincipal().toString();

        System.out.println("用户名是：" + username + "，密码是：" + password);

        // 这里可以做具体的验证方式
//        String userRedisKey = "rediskey";
//        String secretKey = "redisUtils.get(userRedisKey)";
//        if (StringUtils.isBlank(secretKey)) {
//            throw new RuntimeException("Redis");
//        }
//        //解密传入密码
//        try {
//            password = DESUtils.decryptString(secretKey, password);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("加解密出错:{}", e);
//        }

        // 因为 UserNameUserDetailsServiceImpl 返回的数据就是 SecurityUserDetail 类型的
        SecurityUserDetail securityUserDetail = (SecurityUserDetail) userDetails;
        System.out.println(securityUserDetail);
    }


}
