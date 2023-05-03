package com.phantom.gateway.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.phantom.gateway.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义授权管理器，判断用户是否有权限访问
 *
 * @author lei.tan
 */
@Slf4j
@Component
public class CustomReactiveAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {


    @Resource
    private UserMapper userMapper;

    /**
     * 白名单配置
     */
    @Resource
    private WhitelistUrl whitelistUrl;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        ServerWebExchange exchange = authorizationContext.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        // option 请求，全部放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }

        return authentication.flatMap(auth -> {
            log.info("当前用户[{}], 访问路径:[{}]", auth.getName(), path);
            // 查询当前登录的用户是否有权限访问
            Integer count = userMapper.checkAuthorityCount(auth.getName(), path);
            log.info("当前访问路径[{}]查询到的权限配置为[{}]条....", path, count);
            if (count > 0) {
                return Mono.just(new AuthorizationDecision(true));
            } else {
                return Mono.just(new AuthorizationDecision(false));
            }
        });
        /*return authentication
                .filter(Authentication::isAuthenticated)
                .filter(a -> a instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .doOnNext(token -> {
                    Integer count = userMapper.checkAuthorityCount(token.getName(), path);
                    log.info("查询到的url数量: {}", count);
                    log.info("当前请求携带的头信息: {}", token.getToken().getHeaders());
                    log.info("当前请求携带的token信息: {}", token.getTokenAttributes());
                    log.info("当前请求携带的token信息6: {}", token.getName());
                })
                .flatMapIterable(AbstractAuthenticationToken::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authority -> Objects.equals(authority, authorities))
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));*/
    }
}
