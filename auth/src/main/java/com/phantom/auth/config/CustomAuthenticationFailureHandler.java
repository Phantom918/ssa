package com.phantom.auth.config;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证失败处理
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/7/16 01:45
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("CustomAuthenticationFailureHandler: [认证异常]自定义处理.....");
        OAuth2AuthenticationException authenticationException = (OAuth2AuthenticationException) exception;
        log.error("错误原因:[{}]", authenticationException.getError());
        log.info("认证异常", exception);
        // response.setHeader("Access-Control-Allow-Origin", "*");
        // response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        // response.setHeader("Content-Type", "application/json;charset=utf-8");
        // 设置状态码为 401 以及请求头
        response.setStatus(HttpStatus.FORBIDDEN.value());
        // 组装数据返回
        Map<String, Object> result = new HashMap<>(3);
        result.put("status", HttpStatus.FORBIDDEN.value());
        result.put("error", authenticationException.getError());
        result.put("message", "登录认证失败！" + authenticationException.getMessage());
        response.getWriter().write(JSON.toJSONString(result));
        // response.getWriter().flush();
    }
}
