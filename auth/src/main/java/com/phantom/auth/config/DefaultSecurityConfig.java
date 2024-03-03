package com.phantom.auth.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
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
                                form
                                        // 自定义登录页面 url
                                        .loginPage("/login").permitAll()
                        // 自定义登录成功后跳转的 url
                        // .defaultSuccessUrl("http://")
                        // 自定义登录失败后跳转的 url
                        // .failureUrl("http://")
                        // 自定义登录表单提交 url
                        // .loginProcessingUrl("/login")
                        // 自定义登录表单用户名参数名
                        // .usernameParameter("username")
                        // 自定义登录表单密码参数名
                        // .passwordParameter("password")
                )
                .logout(logout -> logout
                        // 自定义退出登录 url
                        .logoutUrl("/logout")
                        // 退出登录成功后跳转的 url
                        .logoutSuccessUrl("http://127.0.0.1:8000")
                        // 退出登录后删除cookie
                        // .deleteCookies("JSESSIONID")
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                /* .sessionManagement(session ->
                    session
                            .disable()
                        // 无效session跳转地址
                        // .invalidSessionUrl("/login")
                        // session并发控制
                        // .maximumSessions(1)
                        // session并发控制，阻止新的登录
                        // .maxSessionsPreventsLogin(true)
                        // session并发控制，session过期策略
                        // .expiredSessionStrategy()
                        // session并发控制，session过期跳转地址
                        // .expiredUrl("/login")
                ) */
        ;

        return http.build();
    }

}
