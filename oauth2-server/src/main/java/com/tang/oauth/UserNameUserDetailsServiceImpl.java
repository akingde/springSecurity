package com.tang.oauth;

import com.tang.entry.UserInfo;
import com.tang.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 通过用户名进行认证
 */
@Service
public class UserNameUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo userInfo = userInfoService.getByUserName(username);
        if (userInfo == null) {
            throw new RuntimeException("用户：" + username + "不存在！");
        }

        // 可以从 authentication.getUserAuthentication().getPrincipal(); 中获取这里设置的用户信息
        SecurityUserDetail securityUserDetail = new SecurityUserDetail(
                userInfo.getUserName(), userInfo.getPassword(), Collections.emptyList());
        securityUserDetail.setEmail(userInfo.getEmail());
        securityUserDetail.setTenantId("xxx");

        return securityUserDetail;
    }
}
