package com.tang.controller;

import com.tang.entry.UserInfo;
import com.tang.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 访问控制层
 */
@RestController
@RequestMapping("/server")
public class UserInfoController {

    @Autowired
    private UserInfoService service;

    @GetMapping("/userName/{userName}")
    public String getByUserName(@PathVariable String userName) {
        UserInfo userInfo = service.getByUserName(userName);
        if (userInfo == null) {
            return "没有用户：" + userName;
        } else {
            return userInfo.toString();
        }
    }

}
