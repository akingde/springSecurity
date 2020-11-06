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
public class MobileUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        UserInfo userInfo = userInfoService.getByUserName(s);
        if (userInfo == null) {
            throw new RuntimeException("用户：" + s + "不存在！");
        }

        SecurityUserDetail securityUserDetail = new SecurityUserDetail(userInfo.getUserName(), userInfo.getPassword(), Collections.emptyList());
        securityUserDetail.setEmail(userInfo.getEmail());
        securityUserDetail.setTenantId("xxx");

        return securityUserDetail;
    }
}
