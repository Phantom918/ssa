package com.phantom.auth.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Joe Grandja
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {


    /**
     * 白名单
     */
    @Resource
    private WhitelistUrl whitelistUrl;

    /**
     * spring boot默认将/**静态资源访问映射到以下目录：
     * classpath:/static
     * classpath:/public
     * classpath:/resources
     * classpath:/META-INF/resources
     * 这四个目录的访问优先级：META-INF/resources > resources > static > public
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // 解决非thymeleaf的form表单提交被拦截问题
                .csrf().disable()
                // 基于JWT令牌，无需Session
                // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // .and()
                // 白名单以及认证配置
                .authorizeHttpRequests(authorize ->
                        authorize
                                // 放行白名单
                                .requestMatchers(whitelistUrl.getUrls().toArray(new String[0])).permitAll()
                                .anyRequest().authenticated()
                )
                // 默认登录页面
                // .formLogin(withDefaults())
                .formLogin(form ->
                        // 自定义登录页面 url
                        form.loginPage("/login").permitAll()
                )
                .logout().logoutSuccessUrl("http://127.0.0.1:8000")
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }

}
