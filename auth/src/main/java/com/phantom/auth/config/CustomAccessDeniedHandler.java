package com.phantom.auth.config;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义授权异常处理
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/7/16 00:37
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("[授权异常]自定义处理.....");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        // response.setHeader("Content-Type", "application/json;charset=utf-8");
        // 设置状态码为 403 以及请求头
        response.setStatus(HttpStatus.FORBIDDEN.value());
        // 组装数据返回
        Map<String, Object> result = new HashMap<>(3);
        result.put("status", HttpStatus.FORBIDDEN.value());
        result.put("error", accessDeniedException.getMessage());
        result.put("message", "授权校验失败，请确认是否有权限访问当前资源！");
        response.getWriter().write(JSON.toJSONString(result));
        // response.getWriter().flush();
    }
}
