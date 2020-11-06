package com.tang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.entry.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户信息访问
 */
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    UserInfo getByUserName(@Param("userName") String userName);

    UserInfo getByMobile(@Param("mobile") String mobile);
}
