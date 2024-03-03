package com.phantom.auth.web;

import jakarta.annotation.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;

/**
 * 用户信息接口
 *
 * @author lei.tan
 */
@RestController
@RequestMapping("/oauth2")
public class EndPointController {

    @Resource
    private CustomApplicationObjectSupport customApplicationObjectSupport;

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/user")
    public Authentication oauth2UserInfo() {
        // customApplicationObjectSupport.getApplicationContext().getBean()
        // ContextLoader.getCurrentWebApplicationContext().getBean()
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("无有效认证用户！");
        }
        return authentication;
    }
}
