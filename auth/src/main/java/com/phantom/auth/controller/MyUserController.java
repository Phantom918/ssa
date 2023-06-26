package com.phantom.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录成功后获取用户信息接口
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/6/25 23:26
 */
@RestController
@RequestMapping("/my1111/oauth2")
public class MyUserController {

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/user")
    public Authentication oauth2UserInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new RuntimeException("无有效认证用户！");
        }
        return authentication;
    }


}
