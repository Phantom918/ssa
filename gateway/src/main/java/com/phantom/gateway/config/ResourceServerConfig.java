package com.phantom.gateway.config;

import com.phantom.gateway.filter.IgnoreUrlsRemoveJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 资源服务器配置
 *
 * @author lei.tan
 */
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    /**
     * 白名单配置
     */
    @jakarta.annotation.Resource
    private WhitelistUrl whitelistUrl;

    @Autowired
    private CustomReactiveAuthorizationManager customReactiveAuthorizationManager;

    @Autowired
    private IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        http
                // 资源服务
                .oauth2ResourceServer(resourceServer -> resourceServer
                        // 认证成功后，没有权限操作
                        .accessDeniedHandler(new CustomServerAccessDeniedHandler())
                        // 未认证前，发生认证异常比如token过期，token不合法
                        .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint())
                        // 将字符串token转换成认证对象
                        //.bearerTokenConverter(new CustomServerBearerTokenAuthenticationConverter())
                        .jwt()
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        .jwtDecoder(jwtDecoder())
                )
                // 授权配置
                .authorizeExchange(authorizeExchange -> authorizeExchange
                        // 白名单配置
                        .pathMatchers(whitelistUrl.getUrls().toArray(new String[0])).permitAll()
                        // 其余的请求都交由此处进行权限判断处理
                        .anyExchange().access(customReactiveAuthorizationManager)
                )
                // 异常处理
                .exceptionHandling(exceptionConfigurer -> exceptionConfigurer
                        // 拒绝访问
                        .accessDeniedHandler(new CustomServerAccessDeniedHandler())
                        // 认证失败
                        .authenticationEntryPoint(new CustomServerAuthenticationEntryPoint())
                )
                // 添加过滤器
                .csrf().disable()
                //.addFilterAfter(new TokenTransferFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
        ;


        return http.build();
    }

    /**
     * 从jwt令牌中获取认证对象
     */
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        // 从jwt 中获取该令牌可以访问的权限
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 取消权限的前缀，默认会加上SCOPE_
        authoritiesConverter.setAuthorityPrefix("");
        // 从哪个字段中获取权限
        authoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // 从哪个字段中获取用户名
        jwtAuthenticationConverter.setPrincipalClaimName(JwtClaimNames.SUB);
        // 设置权限转换器
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * 解码jwt
     */
    public ReactiveJwtDecoder jwtDecoder() {
        try {
            Resource resource = new FileSystemResource("/Users/leitan/WorkSpace/IdeaSpace/ssa/new-authoriza-server-public-key.pem");
            String publicKeyStr = String.join("", Files.readAllLines(resource.getFile().toPath()));
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            return NimbusReactiveJwtDecoder.withPublicKey(rsaPublicKey)
                    .signatureAlgorithm(SignatureAlgorithm.RS256)
                    .build();
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
