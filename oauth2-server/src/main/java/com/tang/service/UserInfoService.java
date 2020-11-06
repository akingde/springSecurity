package com.tang.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.entry.UserInfo;
import com.tang.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 服务层
 */
@Service
public class UserInfoService extends ServiceImpl<UserInfoMapper, UserInfo> {

    @Autowired
    private UserInfoMapper mapper;

    public UserInfo getByUserName(String userName) {
        return mapper.getByUserName(userName);
    }

    public UserInfo getByMobile(String mobile) {
        return mapper.getByMobile(mobile);
    }

}
